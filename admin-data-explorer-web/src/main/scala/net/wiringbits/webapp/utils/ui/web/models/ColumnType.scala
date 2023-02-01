package net.wiringbits.webapp.utils.ui.web.models

import net.wiringbits.webapp.utils.api.models.AdminGetTables.Response.TableColumn

sealed trait ColumnType extends Product with Serializable

object ColumnType {
  case object Date extends ColumnType
  case object Text extends ColumnType
  case object Email extends ColumnType
  case object Image extends ColumnType
  case object Number extends ColumnType
  case class Reference(referencedTable: String, source: String) extends ColumnType

  def fromTableField(column: TableColumn): ColumnType = {
    val isEmail = column.name.contains("email")
    val isDate = column.`type`.equals("timestamptz")
    val isImage = column.`type`.equals("bytea")
    val isInt = column.`type`.equals("integer")
    val isNumeric = column.`type`.contains("numeric")
    val default = column.reference
      .map { reference => ColumnType.Reference(reference.referencedTable, reference.referenceField) }
      .getOrElse(ColumnType.Text)

    if (isEmail)
      ColumnType.Email
    else if (isDate)
      ColumnType.Date
    else if (isImage)
      ColumnType.Image
    else if (isInt || isNumeric)
      ColumnType.Number
    else default
  }
}
