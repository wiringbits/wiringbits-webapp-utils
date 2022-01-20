package net.wiringbits.webapp.utils.admin.services

import net.wiringbits.webapp.utils.admin.config.DataExplorerSettings
import net.wiringbits.webapp.utils.admin.repositories.DatabaseTablesRepository
import net.wiringbits.webapp.utils.admin.utils.models.pagination.PaginatedQuery
import net.wiringbits.webapp.utils.api.models.*

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AdminService @Inject() (
    databaseTablesRepository: DatabaseTablesRepository,
    tableSettings: DataExplorerSettings
)(implicit
    ec: ExecutionContext
) {

  def tables(): Future[AdminGetTables.Response] = {
    for {
      tables <- databaseTablesRepository.getTablesInSettings(tableSettings)
      items = tables.map { x =>
        AdminGetTables.Response.DatabaseTable(
          name = x.name
        )
      }
    } yield AdminGetTables.Response(items)
  }

  def tableMetadata(tableName: String, pagination: PaginatedQuery): Future[AdminGetTableMetadata.Response] = {
    for {
      _ <- validate(tableName, pagination)
      tableMetadata <- databaseTablesRepository.getTableMetadata(tableName, pagination)
    } yield AdminGetTableMetadata.Response(
      name = tableMetadata.data.name,
      fields = tableMetadata.data.fields.map(x => AdminGetTableMetadata.Response.TableField(x.name, x.`type`)),
      rows = tableMetadata.data.rows.map(x =>
        AdminGetTableMetadata.Response.TableRow(x.data.map(_.value).map(AdminGetTableMetadata.Response.Cell.apply))
      ),
      offSet = tableMetadata.offset.int,
      limit = tableMetadata.limit.int,
      count = tableMetadata.total.int
    )
  }

  def find(tableName: String, id: String): Future[AdminFindTable.Response] = {
    for {
      _ <- validateTableName(tableName)
      (row, fields) <- databaseTablesRepository.find(tableName, id)
    } yield AdminFindTable.Response(
      row = AdminGetTableMetadata.Response.TableRow(
        row.data.map(_.value).map(AdminGetTableMetadata.Response.Cell.apply)
      ),
      fields = fields.map(x => AdminGetTableMetadata.Response.TableField(x.name, x.`type`))
    )
  }

  def create(tableName: String, request: AdminCreateTable.Request): Future[Unit] = {
    val body = request.data
    val validate = for {
      _ <- validateTableName(tableName)
      _ <- validateTableFields(tableName, body)
      mandatoryFields <- databaseTablesRepository.getMandatoryFields(tableName)
      mandatoryFieldNames = mandatoryFields.map(_.name)
    } yield
      if (mandatoryFieldNames.forall(request.data.contains)) ()
      else
        throw new RuntimeException(
          s"There are missing fields: ${mandatoryFieldNames.filterNot(request.data.contains).mkString(", ")}"
        )

    for {
      _ <- validate
      _ <- databaseTablesRepository.create(tableName, body)
    } yield ()
  }

  def update(tableName: String, ID: String, request: AdminUpdateTable.Request): Future[Unit] = {
    val validate = Future {
      if (request.data.isEmpty) throw new RuntimeException(s"You need to send some data")
      else ()
    }

    val body = request.data
    for {
      _ <- validate
      _ <- validateTableName(tableName)
      _ <- validateTableFields(tableName, body)
      _ <- databaseTablesRepository.update(tableName, ID, body)
    } yield ()
  }

  def delete(tableName: String, ID: String): Future[Unit] = {
    for {
      _ <- validateTableName(tableName)
      _ <- databaseTablesRepository.delete(tableName, ID)
    } yield ()
  }

  private def validateTableFields(tableName: String, body: Map[String, String]): Future[Unit] = {
    for {
      fields <- databaseTablesRepository.getTableFields(tableName)
      fieldsNames = fields.map(_.name)
      requestFields = body.keys
      exists = requestFields.forall(fieldsNames.contains)
    } yield if (exists) () else throw new RuntimeException(s"A field doesn't correspond to this table schema")
  }

  private def validate(tableName: String, pagination: PaginatedQuery): Future[Unit] = {
    for {
      _ <- Future {
        validatePagination(pagination)
      }
      _ <- validateTableName(tableName)
    } yield ()
  }

  private def validateTableName(tableName: String): Future[Unit] = {
    for {
      tables <- databaseTablesRepository.getTablesInSettings(tableSettings)
      exists = tables.exists(_.name == tableName)
    } yield
      if (exists) () else throw new RuntimeException(s"Unexpected error because the DB table wasn't found: $tableName")
  }

  private def validatePagination(pagination: PaginatedQuery): Unit = {
    if (0 > pagination.offset.int || 0 > pagination.limit.int) {
      throw new RuntimeException(s"You can't query a table using negative numbers as a limit or offset")
    } else ()
  }
}
