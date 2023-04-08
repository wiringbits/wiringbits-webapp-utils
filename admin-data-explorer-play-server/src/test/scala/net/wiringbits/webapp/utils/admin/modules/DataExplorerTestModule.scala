package net.wiringbits.webapp.utils.admin.modules

import com.google.inject.{AbstractModule, Provides}
import net.wiringbits.webapp.utils.admin.config.{DataExplorerSettings, TableSettings, PrimaryKeyDataType}

class DataExplorerTestModule extends AbstractModule {

  @Provides()
  def dataExplorerSettings: DataExplorerSettings = {
    DataExplorerSettings("http://localhost:9000", settings)
  }

  val settings: List[TableSettings] = List(
    TableSettings("users", "user_id"), // "UUID" default
    TableSettings(
      tableName = "uuid_table",
      primaryKeyField = "id",
      primaryKeyDataType = PrimaryKeyDataType.UUID
    ), // explicit default
    TableSettings(tableName = "serial_table", primaryKeyField = "id", primaryKeyDataType = PrimaryKeyDataType.Serial),
    TableSettings(
      tableName = "big_serial_table",
      primaryKeyField = "id",
      primaryKeyDataType = PrimaryKeyDataType.BigSerial
    ),
    TableSettings(
      tableName = "serial_table_overflow",
      primaryKeyField = "id",
      primaryKeyDataType = PrimaryKeyDataType.Serial
    ),
    TableSettings(
      tableName = "big_serial_table_overflow",
      primaryKeyField = "id",
      primaryKeyDataType = PrimaryKeyDataType.BigSerial
    )
  )
}
