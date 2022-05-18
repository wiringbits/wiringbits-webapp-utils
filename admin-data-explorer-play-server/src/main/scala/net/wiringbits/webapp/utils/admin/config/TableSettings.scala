package net.wiringbits.webapp.utils.admin.config

case class TableSettings(tableName: String, primaryKeyField: String, hiddenColumns: List[String] = List.empty)
