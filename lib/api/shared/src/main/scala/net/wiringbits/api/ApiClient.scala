package net.wiringbits.api

import net.wiringbits.api.models._
import play.api.libs.json._
import sttp.client._
import sttp.model._

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait ApiClient {
  def adminGetTables(): Future[AdminGetTablesResponse]
  def adminGetTableMetadata(tableName: String, offset: Int, limit: Int): Future[AdminGetTableMetadataResponse]

  def adminFind(tableName: String, ID: String): Future[AdminFindTableResponse]

  def adminCreate(tableName: String, request: AdminCreateTableRequest): Future[AdminCreateTableResponse]

  def adminUpdate(
      tableName: String,
      ID: String,
      request: AdminUpdateTableRequest
  ): Future[AdminUpdateTableResponse]

  def adminDelete(tableName: String, ID: String): Future[AdminDeleteTableResponse]
}

object ApiClient {
  case class Config(serverUrl: String)

  private def asJson[R: Reads] = {
    asString
      .map {
        case Right(response) =>
          // handles 2xx responses
          Success(response)
        case Left(response) =>
          // handles non 2xx responses
          Try {
            val json = Json.parse(response)
            // TODO: Unify responses to match the play error format
            json
              .asOpt[ErrorResponse]
              .orElse {
                json
                  .asOpt[PlayErrorResponse]
                  .map(model => ErrorResponse(model.error.message))
              }
              .getOrElse(throw new RuntimeException(s"Unexpected JSON response: $response"))
          } match {
            case Failure(exception) =>
              println(s"Unexpected response: ${exception.getMessage}")
              exception.printStackTrace()
              Failure(new RuntimeException(s"Unexpected response, please try again in a minute"))
            case Success(value) =>
              Failure(new RuntimeException(value.error))
          }
      }
      .map { t =>
        t.map(Json.parse).map(_.as[R])
      }
  }

  // TODO: X-Authorization header is being used to keep the nginx basic-authentication
  // once that's removed, Authorization header can be used instead.
  class DefaultImpl(config: Config)(implicit
      backend: SttpBackend[Future, Nothing, Nothing],
      ec: ExecutionContext
  ) extends ApiClient {

    private val ServerAPI = sttp.model.Uri
      .parse(config.serverUrl)
      .getOrElse(throw new RuntimeException("Invalid server url"))

    private def prepareRequest[R: Reads] = {
      basicRequest
        .contentType(MediaType.ApplicationJson)
        .response(asJson[R])
    }

    override def adminGetTables(): Future[AdminGetTablesResponse] = {
      val path = ServerAPI.path :+ "admin" :+ "tables"
      val uri = ServerAPI.path(path)

      prepareRequest[AdminGetTablesResponse]
        .get(uri)
        .send()
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def adminGetTableMetadata(
        tableName: String,
        offset: Int,
        limit: Int
    ): Future[AdminGetTableMetadataResponse] = {
      val path = ServerAPI.path :+ "admin" :+ "tables" :+ tableName
      val parameters = Map(
        "offset" -> offset.toString,
        "limit" -> limit.toString
      )
      val uri = ServerAPI.path(path).params(parameters)

      prepareRequest[AdminGetTableMetadataResponse]
        .get(uri)
        .send()
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def adminFind(tableName: String, ID: String): Future[AdminFindTableResponse] = {
      val path = ServerAPI.path :+ "admin" :+ "tables" :+ tableName :+ ID
      val uri = ServerAPI.path(path)

      prepareRequest[AdminFindTableResponse]
        .get(uri)
        .send()
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def adminCreate(tableName: String, request: AdminCreateTableRequest): Future[AdminCreateTableResponse] = {
      val path = ServerAPI.path :+ "admin" :+ "tables" :+ tableName
      val uri = ServerAPI.path(path)

      prepareRequest[AdminCreateTableResponse]
        .post(uri)
        .body(Json.toJson(request).toString())
        .send()
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def adminUpdate(
        tableName: String,
        ID: String,
        request: AdminUpdateTableRequest
    ): Future[AdminUpdateTableResponse] = {
      val path = ServerAPI.path :+ "admin" :+ "tables" :+ tableName :+ ID
      val uri = ServerAPI.path(path)

      prepareRequest[AdminUpdateTableResponse]
        .put(uri)
        .body(Json.toJson(request).toString())
        .send()
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def adminDelete(tableName: String, ID: String): Future[AdminDeleteTableResponse] = {
      val path = ServerAPI.path :+ "admin" :+ "tables" :+ tableName :+ ID
      val uri = ServerAPI.path(path)

      prepareRequest[AdminDeleteTableResponse]
        .delete(uri)
        .send()
        .map(_.body)
        .flatMap(Future.fromTry)
    }
  }
}
