package net.wiringbits.webapp.utils.admin.tasks

import net.wiringbits.webapp.utils.admin.config.DataExplorerSettings
import net.wiringbits.webapp.utils.admin.repositories.daos.DatabaseTablesDAO
import net.wiringbits.webapp.utils.admin.repositories.models.{DatabaseTable, TableField}
import org.slf4j.LoggerFactory
import play.api.db.Database

import javax.inject.Inject
import scala.util.{Failure, Success, Try}

/** Given that the use provides the tables used by the data explorer, it is important to make sure those tables exist in
  * the database.
  *
  * This task loads the available tables from the database and compares those to the ones provided by the user-defined
  * config, failing when there is a mismatch.
  *
  * @param database
  * @param settings
  */
class DataExplorerConfigValidatorTask @Inject() (
    database: Database,
    settings: DataExplorerSettings
) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  start()

  def start(): Unit = {
    logger.info("Running task")
    Try(run()) match {
      case Failure(exception) => logger.error("Data explorer settings validation failed", exception)
      case Success(_) => logger.info("Data explorer settings validated")
    }
  }

  private def run(): Unit = {
    database.withConnection { implicit conn =>
      val tables = DatabaseTablesDAO.all()
      for (settingsTable <- settings.tables) {
        logger.info(s"Verifying ${settingsTable.tableName}")
        val fields = DatabaseTablesDAO.getTableFields(settingsTable.tableName)
        validateTableName(settingsTable.tableName, tables)
        validatePrimaryKeyFieldName(settingsTable.primaryKeyField, fields)
      }
    }
  }

  private def validateTableName(tableName: String, tablesInDB: List[DatabaseTable]): Unit = {
    if (tablesInDB.exists(_.name == tableName)) ()
    else
      throw new RuntimeException(
        s"$tableName not found, available tables = ${tablesInDB.mkString(", ")}"
      )
  }

  private def validatePrimaryKeyFieldName(idFieldName: String, fields: List[TableField]): Unit = {
    val exists = fields.exists(_.name == idFieldName)
    if (exists) ()
    else throw new RuntimeException(s"The provided id on DataExplorer settings doesn't exists: ${idFieldName}")
  }
}
