package net.wiringbits.webapp.utils.ui.web.components

import japgolly.scalajs.react.vdom.VdomNode
import net.wiringbits.webapp.utils.api.models.AdminGetTables
import net.wiringbits.webapp.utils.api.models.AdminGetTables.Response.TableField
import net.wiringbits.webapp.utils.ui.web.facades.reactadmin.{List => ComponentList, _}
import net.wiringbits.webapp.utils.ui.web.models.FieldDataType

object TableFieldsList {

  def component(response: AdminGetTables.Response.DatabaseTable) = {
    // TODO: avoid this
    val fieldTypes: List[(FieldDataType, String)] = response.fields.map { tableField =>
      (getFieldType(tableField), tableField.name)
    }
    val fields: Seq[VdomNode] = fieldTypes.map { fieldType =>
      fieldType._1 match {
        case FieldDataType.Date => DateField(_.source := fieldType._2)
        case FieldDataType.Text => TextField(_.source := fieldType._2)
        case FieldDataType.Email => EmailField(_.source := fieldType._2)
        case FieldDataType.Reference(reference) =>
          ReferenceField(_.source := fieldType._2, _.reference := reference)(TextField(_.source := "id"))
      }
    }

    ComponentList()(
      Datagrid(_.rowClick := "edit")(
        fields: _*
      )
    )
    ReactAdmin.ListGuesser
  }

  private def getFieldType(tableField: TableField): FieldDataType = {
    val isEmail = tableField.name.startsWith("email")
    val isDate = tableField.`type`.equals("timestamptz")
    // TODO: check type
    val isReferenceField = tableField.name.contains("id") && !tableField.name.equals("id")
    if (isEmail)
      FieldDataType.Email
    else if (isDate)
      FieldDataType.Date
    else if (isReferenceField) {
      // TODO: Fix this
      FieldDataType.Reference("users")
    } else
      FieldDataType.Text
  }
}
