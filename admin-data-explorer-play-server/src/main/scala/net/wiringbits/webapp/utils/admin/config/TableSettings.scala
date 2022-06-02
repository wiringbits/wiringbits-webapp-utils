package net.wiringbits.webapp.utils.admin.config

/** @param tableName
  *   name of table in database
  * @param primaryKeyField
  *   primary key identifier of table
  * @param referenceField
  *   field that react-admin shows for foreign key references instead of primary key
  * @param hiddenColumns
  *   columns that the API won't return when the data is queried (for example: user password)
  * @param nonEditableColumns
  *   columns that aren't editable (disabled) via react-admin
  */

case class TableSettings(
    tableName: String,
    primaryKeyField: String,
    referenceField: Option[String] = None,
    hiddenColumns: List[String] = List.empty,
    nonEditableColumns: List[String] = List.empty,
    canBeDeleted: Boolean = true
)
