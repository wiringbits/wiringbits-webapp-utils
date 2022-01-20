package net.wiringbits.webapp.utils.admin.repositories

import net.wiringbits.webapp.utils.admin.config.DataExplorerSettings
import net.wiringbits.webapp.utils.admin.executors.DatabaseExecutionContext
import net.wiringbits.webapp.utils.admin.repositories.daos.DatabaseTablesDAO
import net.wiringbits.webapp.utils.admin.repositories.models.{DatabaseTable, TableField, TableMetadata, TableRow}
import net.wiringbits.webapp.utils.admin.utils.models.pagination.{PaginatedQuery, PaginatedResult}
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

  def getTablesInSettings(tableSettings: DataExplorerSettings): Future[List[DatabaseTable]] = Future {
    DatabaseTablesDAO.getTablesInSettings(tableSettings)
  }

  def getTableFields(tableName: String): Future[List[TableField]] = Future {
    database.withConnection { implicit conn =>
      DatabaseTablesDAO.getTableFields(tableName)
    }
  }

  def getMandatoryFields(tableName: String): Future[List[TableField]] = Future {
    database.withConnection { implicit conn =>
      DatabaseTablesDAO.getMandatoryFields(tableName, tableSettings)
    }
  }

  def getTableMetadata(tableName: String, pagination: PaginatedQuery): Future[PaginatedResult[TableMetadata]] = Future {
    database.withConnection { implicit conn =>
      val fields = DatabaseTablesDAO.getTableFields(tableName)
      DatabaseTablesDAO.getTableData(tableName, fields, pagination, tableSettings);
    }
  }

  def find(tableName: String, id: String): Future[(TableRow, List[TableField])] = Future {
    database.withConnection { implicit conn =>
      DatabaseTablesDAO.find(tableName, id, tableSettings);
    }
  }

  def create(tableName: String, body: Map[String, String]): Future[Unit] = Future {
    database.withConnection { implicit conn =>
      DatabaseTablesDAO.create(tableName, body, tableSettings);
    }
  }

  def update(tableName: String, id: String, body: Map[String, String]): Future[Unit] =
    Future {
      database.withConnection { implicit conn =>
        DatabaseTablesDAO.update(tableName, id, tableSettings, body);
      }
    }

  def delete(tableName: String, id: String): Future[Unit] =
    Future {
      database.withConnection { implicit conn =>
        DatabaseTablesDAO.delete(tableName, id, tableSettings);
      }
    }
}
