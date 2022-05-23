package net.wiringbits.webapp.utils.admin.config

/** @param tableName
  *   name of table in database
  * @param primaryKeyField
  *   primary key identifier of table
  * @param hiddenColumns
  *   columns that the API won't return when the data is queried (for example: user password)
  * @param disabledColumns
  *   columns that aren't editable (disabled) via react-admin
  */

case class TableSettings(
    tableName: String,
    primaryKeyField: String,
    hiddenColumns: List[String] = List.empty,
    disabledColumns: List[String] = List.empty
)
