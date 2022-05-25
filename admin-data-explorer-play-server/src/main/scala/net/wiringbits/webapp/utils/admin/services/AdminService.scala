package net.wiringbits.webapp.utils.admin.services

import net.wiringbits.webapp.utils.admin.config.DataExplorerSettings
import net.wiringbits.webapp.utils.admin.repositories.DatabaseTablesRepository
import net.wiringbits.webapp.utils.admin.repositories.models.ForeignReference
import net.wiringbits.webapp.utils.admin.utils.{MapStringHideExt, contentRangeHeader}
import net.wiringbits.webapp.utils.admin.utils.models.QueryParameters
import net.wiringbits.webapp.utils.api.models.*
import net.wiringbits.webapp.utils.api.models.AdminGetTables.Response.{TableField, TableReference}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AdminService @Inject() (
    databaseTablesRepository: DatabaseTablesRepository,
    tableSettings: DataExplorerSettings
)(implicit
    ec: ExecutionContext
) {

  def tables(): Future[AdminGetTables.Response] = {
    def getFieldName(fieldName: String, primaryKeyField: String) = {
      val isPrimaryField = fieldName == primaryKeyField
      // NOTE: react-admin requires the id field to be available
      if (isPrimaryField) "id" else fieldName
    }

    def getReferencedTable(tableReferences: List[ForeignReference], fieldName: String): Option[String] = {
      val maybe = tableReferences.filter(_.columnName == fieldName)
      // remove public from string
      maybe.map(_.primaryTable.replace("public.", "")).headOption
    }

    def getFieldReference(tableName: String): TableReference = {
      val maybe = tableSettings.unsafeFindByName(tableName).referenceField
      val referenceField = maybe.getOrElse("id")
      TableReference(referencedTable = tableName, referenceField = referenceField)
    }

    for {
      items <- Future.sequence {
        tableSettings.tables.map { settings =>
          val hiddenColumns = settings.hiddenColumns
          for {
            tableFields <- databaseTablesRepository.getTableFields(settings.tableName)
            tableReferences <- databaseTablesRepository.getTableReferences(settings.tableName)

            visibleFields = tableFields.filterNot(field => hiddenColumns.contains(field.name))
            fields = visibleFields.map { field =>
              val fieldName = getFieldName(field.name, settings.primaryKeyField)
              val editable = !settings.nonEditableColumns.contains(field.name)
              val referencedTableName = getReferencedTable(tableReferences, field.name)
              val reference = referencedTableName.map { getFieldReference }
              TableField(name = fieldName, `type` = field.`type`, editable = editable, reference = reference)
            }
          } yield AdminGetTables.Response.DatabaseTable(
            name = settings.tableName,
            fields = fields,
            primaryKeyName = settings.primaryKeyField,
            filterColumns = settings.filterColumns
          )
        }
      }
    } yield AdminGetTables.Response(items)
  }

  def tableMetadata(
      tableName: String,
      queryParameters: QueryParameters
  ): Future[(List[Map[String, String]], String)] = {
    validateTableName(tableName)
    val settings = tableSettings.unsafeFindByName(tableName)
    val sortParam = queryParameters.sort.fromPrimaryKeyField(settings.primaryKeyField)
    val queryParams = queryParameters.copy(sort = sortParam)

    for {
      _ <- validateQueryParameters(tableName, queryParams)
      tableRows <- databaseTablesRepository.getTableMetadata(tableName, queryParams)
      numberOfRecords <- databaseTablesRepository.numberOfRecords(tableName)
      tableData = tableRows.map(_.data)
      hiddenTableData = tableData.map(data => data.hideData(settings.hiddenColumns))
      contentRange = contentRangeHeader(tableName, queryParams, numberOfRecords)
    } yield (hiddenTableData, contentRange)
  }

  private def validateQueryParameters(tableName: String, params: QueryParameters): Future[Unit] = {
    val validOrdering = List("ASC", "DESC")
    if (!validOrdering.contains(params.sort.ordering))
      throw new RuntimeException("Invalid ordering parameter")
    if (params.pagination.start < 0)
      throw new RuntimeException("You can't query a table using negative numbers")
    if (params.pagination.start > params.pagination.end)
      throw new RuntimeException("The start parameter can't be bigger than the end")
    for {
      _ <- validateTableField(tableName, params.sort.field)
    } yield ()
  }

  def find(tableName: String, primaryKeyValue: String): Future[Map[String, String]] = {
    val validations = Future {
      validateTableName(tableName)
    }

    for {
      _ <- validations
      tableRow <- databaseTablesRepository.find(tableName, primaryKeyValue)
      settings = tableSettings.unsafeFindByName(tableName)
      tableData = tableRow.data
      hiddenData = tableData.hideData(settings.hiddenColumns)
    } yield hiddenData
  }

  def find(tableName: String, primaryKeyValues: List[String]): Future[List[Map[String, String]]] = {
    val validations = Future {
      validateTableName(tableName)
    }

    for {
      _ <- validations
      tableRows <- Future.sequence {
        primaryKeyValues.map { primaryKeyValue =>
          databaseTablesRepository.find(tableName, primaryKeyValue)
        }
      }
      settings = tableSettings.unsafeFindByName(tableName)
      tableData = tableRows.map(_.data)
      maskedTableData = tableData.map(data => data.hideData(settings.hiddenColumns))
    } yield maskedTableData
  }

  def create(tableName: String, request: AdminCreateTable.Request): Future[Unit] = {
    val body = request.data
    val validations = {
      validateTableName(tableName)
      for {
        _ <- validateTableFields(tableName, body)
        _ <- validateMissingFields(tableName, body)
      } yield ()
    }

    for {
      _ <- validations
      _ <- databaseTablesRepository.create(tableName, body)
    } yield ()
  }

  private def validateMissingFields(tableName: String, data: Map[String, String]): Future[Unit] = {
    for {
      mandatoryFields <- databaseTablesRepository.getMandatoryFields(tableName)
      mandatoryFieldNames = mandatoryFields.map(_.name)
      missingFields = mandatoryFieldNames.forall(data.contains)
    } yield
      if (missingFields) ()
      else
        throw new RuntimeException(
          s"There are missing fields: ${mandatoryFieldNames.filterNot(data.contains).mkString(", ")}"
        )
  }

  def update(tableName: String, primaryKeyValue: String, body: Map[String, String]): Future[Unit] = {
    val validations = {
      if (body.isEmpty) throw new RuntimeException(s"You need to send data") else ()
      validateTableName(tableName)
      for {
        _ <- validateTableFields(tableName, body)
      } yield ()
    }

    for {
      _ <- validations
      _ <- databaseTablesRepository.update(tableName, primaryKeyValue, body)
    } yield ()
  }

  def delete(tableName: String, primaryKeyValue: String): Future[Unit] = {
    val validations = Future {
      validateTableName(tableName)
    }

    for {
      _ <- validations
      _ <- databaseTablesRepository.delete(tableName, primaryKeyValue)
    } yield ()
  }

  private def validateTableName(tableName: String): Unit = {
    val exists = tableSettings.tables.exists(_.tableName == tableName)
    if (exists) () else throw new RuntimeException(s"Unexpected error because the DB table wasn't found: $tableName")
  }

  private def validateTableFields(tableName: String, body: Map[String, String]): Future[Unit] = {
    for {
      fields <- databaseTablesRepository.getTableFields(tableName)
      fieldsNames = fields.map(_.name)
      requestFields = body.keys
      exists = requestFields.forall(fieldsNames.contains)
    } yield if (exists) () else throw new RuntimeException(s"A field doesn't correspond to this table schema")
  }

  private def validateTableField(tableName: String, fieldName: String): Future[Unit] = {
    for {
      fields <- databaseTablesRepository.getTableFields(tableName)
      fieldNames = fields.map(_.name)
      exists = fieldNames.contains(fieldName)
    } yield if (exists) () else throw new RuntimeException(s"Field $fieldName doesn't exists in $tableName")
  }
}
