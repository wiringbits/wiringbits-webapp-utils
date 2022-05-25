package net.wiringbits.webapp.utils.ui.web.components

import japgolly.scalajs.react.vdom.html_<^._
import net.wiringbits.webapp.utils.api.models.AdminGetTables
import net.wiringbits.webapp.utils.ui.web.facades.reactadmin.{List => AdminList, _}
import net.wiringbits.webapp.utils.ui.web.models.FieldType
import net.wiringbits.webapp.utils.ui.web.utils.ResponseGuesser

object ListGuesser {

  def apply(response: AdminGetTables.Response.DatabaseTable) = {
    val fields = ResponseGuesser.getTypesFromResponse(response)
    val widgetFields: List[VdomNode] = fields.map { field =>
      field.`type` match {
        case FieldType.Date => DateField(_.source := field.name, _.showTime)
        case FieldType.Text => TextField(_.source := field.name)
        case FieldType.Email => EmailField(_.source := field.name)
        case FieldType.Reference(reference, source) =>
          ReferenceField(_.reference := reference, _.source := field.name)(TextField(_.source := source))
      }
    }
    val filters: List[VdomNode] = response.filterColumns.map { columnName =>
      TextInput(_.source := columnName, _.alwaysOn)
    }
    AdminList(_.filters := filters.toVdomArray)(
      Datagrid(_.rowClick := "edit")(widgetFields: _*)
    )
  }
}
