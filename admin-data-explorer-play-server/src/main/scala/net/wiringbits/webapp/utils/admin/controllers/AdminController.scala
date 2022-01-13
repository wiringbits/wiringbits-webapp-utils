package net.wiringbits.webapp.utils.admin.controllers

import net.wiringbits.webapp.utils.api.models.*
import net.wiringbits.webapp.utils.admin.services.AdminService
import net.wiringbits.webapp.utils.admin.utils.models.pagination.{Limit, Offset, PaginatedQuery}
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

// TODO: Remove authentication, which should be provided by each app
class AdminController @Inject() (
    adminService: AdminService
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

  def getTableMetadata(tableName: String, offset: Int, limit: Int) = handleGET { request =>
    val query = PaginatedQuery(Offset(offset), Limit(limit))
    for {
      _ <- adminUser(request)
      _ = logger.info(s"Get metadata for $tableName, offset = $offset, limit = $limit")
      response <- adminService.tableMetadata(tableName, query)
    } yield Ok(Json.toJson(response))
  }

  def find(tableName: String, id: String) = handleGET { request =>
    for {
      _ <- adminUser(request)
      _ = logger.info(s"Get row from $tableName, id = $id")
      response <- adminService.find(tableName, id)
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

  def update(tableName: String, id: String) = handleJsonBody[AdminUpdateTable.Request] { request =>
    val body = request.body
    for {
      _ <- adminUser(request)
      _ = logger.info(s"Update row from $tableName, id = $id, body = ${body.data}")
      _ <- adminService.update(tableName, id, body)
      response = AdminUpdateTable.Response()
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
