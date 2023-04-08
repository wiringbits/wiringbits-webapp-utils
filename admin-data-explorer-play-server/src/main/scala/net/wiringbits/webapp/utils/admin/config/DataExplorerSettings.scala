package net.wiringbits.webapp.utils.admin.config

case class DataExplorerSettings(baseUrl: String, tables: List[TableSettings]) {
  def unsafeFindByName(tableName: String): TableSettings = {
    tables
      .find(_.tableName == tableName)
      .getOrElse(throw new RuntimeException(s"Cannot find settings for table: $tableName"))
  }
}
