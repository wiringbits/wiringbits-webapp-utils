package net.wiringbits.webapp.utils.admin.services

import net.wiringbits.webapp.utils.admin.config.{CustomDataType, DataExplorerSettings, TableSettings}
import net.wiringbits.webapp.utils.admin.repositories.DatabaseTablesRepository
import net.wiringbits.webapp.utils.admin.repositories.models.{ForeignKey, TableData}
import net.wiringbits.webapp.utils.admin.utils.models.QueryParameters
import net.wiringbits.webapp.utils.admin.utils.{MapStringHideExt, contentRangeHeader}
import net.wiringbits.webapp.utils.api.models.*

import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, File}
import java.util.UUID
import javax.imageio.ImageIO
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class AdminService @Inject() (
    databaseTablesRepository: DatabaseTablesRepository,
    tableSettings: DataExplorerSettings
)(implicit
    ec: ExecutionContext
) {

  private def getColumnReference(
      foreignKeys: List[ForeignKey],
      columnName: String
  ): Option[AdminGetTables.Response.TableReference] = {
    val filteredForeignKeys = foreignKeys.filter(_.foreignColumnName == columnName)
    val maybe = filteredForeignKeys.map(_.primaryTable).headOption

    maybe.map { tableName =>
      val maybe = tableSettings.unsafeFindByName(tableName).referenceField
      val referenceField = maybe.getOrElse("id")
      AdminGetTables.Response.TableReference(referencedTable = tableName, referenceField = referenceField)
    }
  }

  def tables(): Future[AdminGetTables.Response] = {
    def getColumnName(fieldName: String, primaryKeyField: String) = {
      val isPrimaryField = fieldName == primaryKeyField
      // NOTE: react-admin requires the id field to be available
      if (isPrimaryField) "id" else fieldName
    }

    for {
      items <- Future.sequence {
        tableSettings.tables.map { settings =>
          val hiddenColumns = settings.hiddenColumns
          for {
            tableColumns <- databaseTablesRepository.getTableColumns(settings.tableName)
            foreignKeys <- databaseTablesRepository.getForeignKeys(settings.tableName)

            visibleColumns = tableColumns.filterNot(column => hiddenColumns.contains(column.name))
            columns = visibleColumns.map { column =>
              val fieldName = getColumnName(column.name, settings.primaryKeyField)
              val isEditable = !settings.nonEditableColumns.contains(column.name)
              val reference = getColumnReference(foreignKeys, column.name)
              val isFilterable = settings.filterableColumns.contains(column.name)
              AdminGetTables.Response.TableColumn(
                name = fieldName,
                `type` = column.`type`,
                editable = isEditable,
                reference = reference,
                filterable = isFilterable
              )
            }
          } yield AdminGetTables.Response.DatabaseTable(
            name = settings.tableName,
            columns = columns,
            primaryKeyName = settings.primaryKeyField,
            canBeDeleted = settings.canBeDeleted
          )
        }
      }
    } yield AdminGetTables.Response(items)
  }

  private def hideData(tableData: TableData, hiddenColumns: List[String]) = {
    tableData.data.hideData(hiddenColumns)
  }

  def tableMetadata(tableName: String, queryParams: QueryParameters): Future[(List[Map[String, String]], String)] = {
    val validations = {
      for {
        _ <- Future(validateTableName(tableName))
      } yield ()
    }

    for {
      _ <- validations
      settings = tableSettings.unsafeFindByName(tableName)
      _ <- validateQueryParameters(tableName, queryParams)
      tableRows <- databaseTablesRepository.getTableMetadata(settings, queryParams)
      numberOfRecords <- databaseTablesRepository.numberOfRecords(tableName)
      hiddenTableData = tableRows.map(data => hideData(data, settings.hiddenColumns))
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
      _ <- validateColumnName(tableName, params.sort.field)
    } yield ()
  }

  def find(tableName: String, primaryKeyValue: String): Future[Map[String, String]] = {
    val validations = Future {
      validateTableName(tableName)
    }

    for {
      _ <- validations
      maybe <- databaseTablesRepository.find(tableName, primaryKeyValue)
      tableRow = maybe.getOrElse(throw new RuntimeException(s"Cannot find item in $tableName with id $primaryKeyValue"))
      settings = tableSettings.unsafeFindByName(tableName)
      hiddenData = hideData(tableRow, settings.hiddenColumns)
    } yield hiddenData
  }

  def find(tableName: String, primaryKeyValues: List[String]): Future[List[Map[String, String]]] = {
    val validations = Future {
      validateTableName(tableName)
    }

    for {
      _ <- validations
      settings = tableSettings.unsafeFindByName(tableName)
      tableRows <- Future.sequence {
        primaryKeyValues.map { primaryKeyValue =>
          for {
            maybe <- databaseTablesRepository.find(tableName, primaryKeyValue)
            tableData = maybe.getOrElse(
              throw new RuntimeException(s"Cannot find item in $tableName with id $primaryKeyValue")
            )
          } yield tableData
        }
      }
      maskedTableData = tableRows.map(x => hideData(x, settings.hiddenColumns))
    } yield maskedTableData
  }

  def create(tableName: String, request: AdminCreateTable.Request): Future[Unit] = {
    val body = request.data
    val validations = {
      validateTableName(tableName)
      for {
        _ <- validateRequestData(tableName, body)
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
        _ <- validateRequestData(tableName, body)
      } yield ()
    }

    for {
      _ <- validations
      _ <- databaseTablesRepository.update(tableName, primaryKeyValue, body)
    } yield ()
  }

  def delete(tableName: String, primaryKeyValue: String): Future[Unit] = {
    val validations = for {
      _ <- Future(validateTableName(tableName))
      settings = tableSettings.unsafeFindByName(tableName)
      _ = if (settings.canBeDeleted) () else throw new RuntimeException(s"Table $tableName resources cannot be deleted")
    } yield ()

    for {
      _ <- validations

      _ <- databaseTablesRepository.delete(tableName, primaryKeyValue)
    } yield ()
  }

  private def validateTableName(tableName: String): Unit = {
    val exists = tableSettings.tables.exists(_.tableName == tableName)
    if (exists) () else throw new RuntimeException(s"Unexpected error because the DB table wasn't found: $tableName")
  }

  private def validateRequestData(tableName: String, body: Map[String, String]): Future[Unit] = {
    for {
      tableColumns <- databaseTablesRepository.getTableColumns(tableName)
      columnNames = tableColumns.map(_.name)
      requestFields = body.keys
      exists = requestFields.forall(columnNames.contains)
    } yield if (exists) () else throw new RuntimeException(s"A field doesn't correspond to this table schema")
  }

  private def validateColumnName(tableName: String, columnName: String): Future[Unit] = {
    for {
      tableColumns <- databaseTablesRepository.getTableColumns(tableName)
      columnNames = tableColumns.map(_.name)
      exists = columnNames.contains(columnName) || columnName == "id"
    } yield if (exists) () else throw new RuntimeException(s"Column $columnName doesn't exists in $tableName")
  }

  def findImage(tableName: String, columnName: String, imageId: String): Future[File] = {
    val validations = {
      for {
        _ <- validateColumnName(tableName, columnName)
        _ <- Future(validateResourceId(imageId))
      } yield ()
    }
    for {
      _ <- validations
      settings = tableSettings.unsafeFindByName(tableName)
      _ = validateImageColumn(settings, columnName)
      maybe <- databaseTablesRepository.getImageData(settings, columnName, imageId)
      imageData = maybe.getOrElse(throw new RuntimeException(s"Image with id $imageId on $tableName doesn't exists"))
      image = createImage(imageData)
    } yield createFile(imageId, image)
  }

  private def validateResourceId(resourceId: String): Unit = {
    val isValid = Try(BigInt(resourceId)).isSuccess || Try(UUID.fromString(resourceId)).isSuccess
    if (isValid) () else throw new RuntimeException(s"Resource id $resourceId is not valid")
  }

  private def validateImageColumn(settings: TableSettings, columnName: String): Unit = {
    val maybe = settings.columnTypeOverrides.find(x => x._1 == columnName && x._2 == CustomDataType.BinaryImage)
    if (maybe.isDefined) () else throw new RuntimeException("This column can't be used as a binary image")
  }

  private def createImage(data: Array[Byte]) = ImageIO.read(new ByteArrayInputStream(data))

  // TODO: avoid assuming that every DB image is on png format
  private def createFile(imageId: String, buffImage: BufferedImage) = {
    val outputFile = File.createTempFile(imageId, ".png")
    ImageIO.write(buffImage, "png", outputFile)
    outputFile
  }
}
