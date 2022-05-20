package net.wiringbits.webapp.utils.ui.web.models

import net.wiringbits.webapp.utils.api.models.AdminGetTables.Response.TableField

sealed trait FieldType extends Product with Serializable

object FieldType {
  case object Date extends FieldType
  case object Text extends FieldType
  case object Email extends FieldType

  def fromTableField(field: TableField): FieldType = {
    val isEmail = field.name.contains("email")
    val isDate = field.`type`.equals("timestamptz")
    // TODO: reference fields
    if (isEmail)
      FieldType.Email
    else if (isDate)
      FieldType.Date
    else
      FieldType.Text
  }
}
