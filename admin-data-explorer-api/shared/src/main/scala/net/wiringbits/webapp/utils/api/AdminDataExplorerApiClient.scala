package net.wiringbits.webapp.utils.api

import net.wiringbits.webapp.utils.api.models._
import play.api.libs.json._
import sttp.client3._
import sttp.model._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait AdminDataExplorerApiClient {
  def getTables(): Future[AdminGetTablesResponse]

  def getTableMetadata(tableName: String, offset: Int, limit: Int): Future[AdminGetTableMetadataResponse]

  def findTable(tableName: String, id: String): Future[AdminFindTableResponse]

  def createItem(tableName: String, request: AdminCreateTableRequest): Future[AdminCreateTableResponse]

  def updateItem(tableName: String, id: String, request: AdminUpdateTableRequest): Future[AdminUpdateTableResponse]

  def deleteItem(tableName: String, id: String): Future[AdminDeleteTableResponse]
}

object AdminDataExplorerApiClient {
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

  class DefaultImpl(config: Config)(implicit
      backend: SttpBackend[Future, _],
      ec: ExecutionContext
  ) extends AdminDataExplorerApiClient {

    private val ServerAPI = sttp.model.Uri
      .parse(config.serverUrl)
      .getOrElse(throw new RuntimeException("Invalid server url"))

    private def prepareRequest[R: Reads] = {
      basicRequest
        .contentType(MediaType.ApplicationJson)
        .response(asJson[R])
    }

    override def getTables(): Future[AdminGetTablesResponse] = {
      val path = ServerAPI.path :+ "admin" :+ "tables"
      val uri = ServerAPI.withPath(path)

      prepareRequest[AdminGetTablesResponse]
        .get(uri)
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def getTableMetadata(
        tableName: String,
        offset: Int,
        limit: Int
    ): Future[AdminGetTableMetadataResponse] = {
      val path = ServerAPI.path :+ "admin" :+ "tables" :+ tableName
      val parameters = Map(
        "offset" -> offset.toString,
        "limit" -> limit.toString
      )
      val uri = ServerAPI
        .withPath(path)
        .addParams(parameters)

      prepareRequest[AdminGetTableMetadataResponse]
        .get(uri)
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def findTable(tableName: String, id: String): Future[AdminFindTableResponse] = {
      val path = ServerAPI.path :+ "admin" :+ "tables" :+ tableName :+ id
      val uri = ServerAPI.withPath(path)

      prepareRequest[AdminFindTableResponse]
        .get(uri)
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def createItem(tableName: String, request: AdminCreateTableRequest): Future[AdminCreateTableResponse] = {
      val path = ServerAPI.path :+ "admin" :+ "tables" :+ tableName
      val uri = ServerAPI.withPath(path)

      prepareRequest[AdminCreateTableResponse]
        .post(uri)
        .body(Json.toJson(request).toString())
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def updateItem(
        tableName: String,
        id: String,
        request: AdminUpdateTableRequest
    ): Future[AdminUpdateTableResponse] = {
      val path = ServerAPI.path :+ "admin" :+ "tables" :+ tableName :+ id
      val uri = ServerAPI.withPath(path)

      prepareRequest[AdminUpdateTableResponse]
        .put(uri)
        .body(Json.toJson(request).toString())
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }

    override def deleteItem(tableName: String, id: String): Future[AdminDeleteTableResponse] = {
      val path = ServerAPI.path :+ "admin" :+ "tables" :+ tableName :+ id
      val uri = ServerAPI.withPath(path)

      prepareRequest[AdminDeleteTableResponse]
        .delete(uri)
        .send(backend)
        .map(_.body)
        .flatMap(Future.fromTry)
    }
  }
}
