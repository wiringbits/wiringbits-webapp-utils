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
  * @param canBeDeleted
  *   indicates if resources from this table can be deleted
  * @param photoColumn
  *   column that has binary image data to be displayed on the page
  * @param primaryKeyDataType
  *   UUID, Serial, or BigSerial primary keys
  */

case class TableSettings(
    tableName: String,
    primaryKeyField: String,
    referenceField: Option[String] = None,
    hiddenColumns: List[String] = List.empty,
    nonEditableColumns: List[String] = List.empty,
    canBeDeleted: Boolean = true,
    photoColumn: Option[String] = None,
    primaryKeyDataType: PrimaryKeyDataType = PrimaryKeyDataType.UUID
)

sealed trait PrimaryKeyDataType extends Product with Serializable
object PrimaryKeyDataType {
  final case object UUID extends PrimaryKeyDataType
  final case object Serial extends PrimaryKeyDataType
  final case object BigSerial extends PrimaryKeyDataType
}
