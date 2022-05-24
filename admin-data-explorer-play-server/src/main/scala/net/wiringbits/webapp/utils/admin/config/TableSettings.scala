package net.wiringbits.webapp.utils.admin.config

/** @param tableName
  *   name of table in database
  * @param primaryKeyField
  *   primary key identifier of table
  * @param referenceField
  *   field that react-admin shows for foreign key references instead of primary key
  * @param hiddenColumns
  *   columns that the API won't return when the data is queried (for example: user password)
  */

case class TableSettings(
    tableName: String,
    primaryKeyField: String,
    // TODO: validate field
    referenceField: Option[String] = None,
    hiddenColumns: List[String] = List.empty
)
