package net.wiringbits.webapp.utils.admin.controllers

import net.wiringbits.webapp.utils.admin.config.DataExplorerSettings
import net.wiringbits.webapp.utils.admin.services.AdminService
import net.wiringbits.webapp.utils.admin.utils.models.QueryParameters
import net.wiringbits.webapp.utils.api.models.*
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

// TODO: Remove authentication, which should be provided by each app
class AdminController @Inject() (
    adminService: AdminService,
    settings: DataExplorerSettings
)(implicit cc: ControllerComponents, ec: ExecutionContext)
    extends AbstractController(cc) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def getTables() = handleGET { request =>
    for {
      _ <- adminUser(request)
      _ = logger.info(s"Get database tables")
      response <- adminService.tables()
    } yield Ok(Json.toJson(response))
  }

  def getTableMetadata(tableName: String, queryParams: QueryParameters) = handleGET { request =>
    for {
      _ <- adminUser(request)
      _ = logger.info(s"Get metadata for $tableName, parameters: $queryParams")
      (response, contentRange) <- adminService.tableMetadata(tableName, queryParams)
    } yield Ok(Json.toJson(response))
      .withHeaders(("Access-Control-Expose-Headers", "Content-Range"), ("Content-Range", contentRange))
  }

  def find(tableName: String, primaryKeyValue: String) = handleGET { request =>
    for {
      _ <- adminUser(request)
      _ = logger.info(s"Get data from $tableName where primaryKey = $primaryKeyValue")
      response <- adminService.find(tableName, primaryKeyValue)
    } yield Ok(Json.toJson(response))
  }

  def find(tableName: String, primaryKeyValues: List[String]) = handleGET { request =>
    for {
      _ <- adminUser(request)
      _ = logger.info(s"Get data from $tableName where primaryKeys = ${primaryKeyValues.mkString(",")}")
      response <- adminService.find(tableName, primaryKeyValues)
    } yield Ok(Json.toJson(response))
  }

  def create(tableName: String) = handleJsonBody[AdminCreateTable.Request] { request =>
    val body = request.body
    for {
      _ <- adminUser(request)
      _ = logger.info(s"Create row in $tableName: ${body.data}")
      _ <- adminService.create(tableName, body)
      response = AdminCreateTable.Response()
    } yield Ok(Json.toJson(response))
  }

  def update(tableName: String, primaryKeyValue: String) = handleJsonBody[Map[String, String]] { request =>
    val primaryKeyFieldName = settings.unsafeFindByName(tableName).primaryKeyField
    val body = request.body.map {
      case ("id", value) => primaryKeyFieldName -> value
      case x => x
    }
    for {
      _ <- adminUser(request)
      _ = logger.info(s"Update row from $tableName where primaryKey = $primaryKeyValue, body = $body")
      _ <- adminService.update(tableName, primaryKeyValue, body)
      response = AdminUpdateTable.Response(id = primaryKeyValue)
    } yield Ok(Json.toJson(response))
  }

  def delete(tableName: String, id: String) = handleGET { request =>
    for {
      _ <- adminUser(request)
      _ = logger.info(s"Delete row from $tableName, id = $id")
      _ <- adminService.delete(tableName, id)
      response = AdminDeleteTable.Response()
    } yield Ok(Json.toJson(response))
  }
}
