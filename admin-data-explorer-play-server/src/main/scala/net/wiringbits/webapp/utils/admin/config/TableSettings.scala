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
  * @param primaryKeyDataType
  *   UUID, Serial, or BigSerial primary keys
  * @param columnTypeOverrides
  *   overrides the data type and converts it, it requires a column name and Text, BinaryImage, Binary
  * @param filterableColumns
  *   columns that are filterable via react-admin
  */

case class TableSettings(
    tableName: String,
    primaryKeyField: String,
    referenceField: Option[String] = None,
    hiddenColumns: List[String] = List.empty,
    nonEditableColumns: List[String] = List.empty,
    canBeDeleted: Boolean = true,
    primaryKeyDataType: PrimaryKeyDataType = PrimaryKeyDataType.UUID,
    columnTypeOverrides: Map[String, CustomDataType] = Map.empty,
    filterableColumns: List[String] = List.empty
)

sealed trait PrimaryKeyDataType extends Product with Serializable
object PrimaryKeyDataType {
  final case object UUID extends PrimaryKeyDataType
  final case object Serial extends PrimaryKeyDataType
  final case object BigSerial extends PrimaryKeyDataType
}

sealed trait CustomDataType extends Product with Serializable
object CustomDataType {
  final case object BinaryImage extends CustomDataType
  // TODO: add support to binary files
  final case object Binary extends CustomDataType
}
