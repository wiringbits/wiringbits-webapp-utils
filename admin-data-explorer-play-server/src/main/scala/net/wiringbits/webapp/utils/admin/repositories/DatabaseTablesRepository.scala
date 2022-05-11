package net.wiringbits.webapp.utils.admin.repositories

import net.wiringbits.webapp.utils.admin.config.DataExplorerSettings
import net.wiringbits.webapp.utils.admin.executors.DatabaseExecutionContext
import net.wiringbits.webapp.utils.admin.repositories.daos.DatabaseTablesDAO
import net.wiringbits.webapp.utils.admin.repositories.models.{DatabaseTable, TableData, TableField}
import net.wiringbits.webapp.utils.admin.utils.models.QueryParameters
import play.api.db.Database

import javax.inject.Inject
import scala.concurrent.Future

class DatabaseTablesRepository @Inject() (database: Database)(implicit
    ec: DatabaseExecutionContext,
    tableSettings: DataExplorerSettings
) {
  def all(): Future[List[DatabaseTable]] = Future {
    database.withConnection { implicit conn =>
      DatabaseTablesDAO.all()
    }
  }

  def getTableFields(tableName: String): Future[List[TableField]] = Future {
    database.withConnection { implicit conn =>
      DatabaseTablesDAO.getTableFields(tableName)
    }
  }

  def getMandatoryFields(tableName: String): Future[List[TableField]] = Future {
    database.withConnection { implicit conn =>
      val primaryKeyField = tableSettings.unsafeFindByName(tableName).primaryKeyField
      DatabaseTablesDAO.getMandatoryFields(tableName, primaryKeyField)
    }
  }

  def getTableMetadata(
      tableName: String,
      queryParameters: QueryParameters
  ): Future[List[TableData]] =
    Future {
      database.withTransaction { implicit conn =>
        val fields = DatabaseTablesDAO.getTableFields(tableName)
        val rows = DatabaseTablesDAO.getTableData(tableName, fields, queryParameters)
        val primaryKeyField = tableSettings.unsafeFindByName(tableName).primaryKeyField
        val fieldNames = fields.map(_.name)
        // react-admin looks for an "id" field instead of "user_id", "user_log_id", etc..
        val updatedNames = fieldNames.updated(fieldNames.indexOf(primaryKeyField), "id")
        rows.map { row =>
          val tableRow = row.convertToMap(updatedNames)
          TableData(tableRow)
        }
      }
    }

  def find(tableName: String, primaryKeyValue: String): Future[TableData] = Future {
    database.withTransaction { implicit conn =>
      val primaryKeyField = tableSettings.unsafeFindByName(tableName).primaryKeyField
      val maybe = DatabaseTablesDAO.find(tableName, primaryKeyField, primaryKeyValue)
      val row = maybe.getOrElse(throw new RuntimeException(s"Cannot find item in $tableName with id $primaryKeyValue"))
      val fields = DatabaseTablesDAO.getTableFields(tableName)

      val fieldNames = fields.map(_.name)
      val updatedNames = fieldNames.updated(fieldNames.indexOf(primaryKeyField), "id")
      val tableRow = row.convertToMap(updatedNames)
      TableData(tableRow)
    }
  }

  def create(tableName: String, body: Map[String, String]): Future[Unit] = Future {
    database.withConnection { implicit conn =>
      val primaryKeyField = tableSettings.unsafeFindByName(tableName).primaryKeyField
      DatabaseTablesDAO.create(tableName, body, primaryKeyField)
    }
  }

  def update(tableName: String, primaryKeyValue: String, body: Map[String, String]): Future[Unit] =
    Future {
      database.withTransaction { implicit conn =>
        val primaryKeyField = tableSettings.unsafeFindByName(tableName).primaryKeyField
        val fields = DatabaseTablesDAO.getTableFields(tableName)
        // transforms Map[String, String] to Map[TableField, String]
        // this is necessary because we want the column type to cast the data
        val fieldsAndValues = body.map { case (key, value) =>
          val field =
            fields.find(_.name == key).getOrElse(throw new RuntimeException(s"Invalid property in body request: $key"))
          (field, value)
        }
        DatabaseTablesDAO.update(tableName, fieldsAndValues, primaryKeyField, primaryKeyValue)
      }
    }

  def delete(tableName: String, primaryKeyValue: String): Future[Unit] =
    Future {
      database.withConnection { implicit conn =>
        val primaryKeyField = tableSettings.unsafeFindByName(tableName).primaryKeyField
        DatabaseTablesDAO.delete(tableName, primaryKeyField, primaryKeyValue)
      }
    }

  def numberOfRecords(tableName: String): Future[Int] = Future {
    database.withConnection { implicit conn =>
      DatabaseTablesDAO.countRecordsOnTable(tableName)
    }
  }
}
