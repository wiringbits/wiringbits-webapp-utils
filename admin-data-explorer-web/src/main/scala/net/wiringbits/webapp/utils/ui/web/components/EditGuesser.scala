package net.wiringbits.webapp.utils.ui.web.components

import japgolly.scalajs.react.vdom.html_<^.VdomNode
import net.wiringbits.webapp.utils.api.models.AdminGetTables
import net.wiringbits.webapp.utils.ui.web.facades.reactadmin._
import net.wiringbits.webapp.utils.ui.web.models.ColumnType
import net.wiringbits.webapp.utils.ui.web.utils.ResponseGuesser

object EditGuesser {

  def apply(response: AdminGetTables.Response.DatabaseTable) = {
    val fields = ResponseGuesser.getTypesFromResponse(response)
    val inputs: List[VdomNode] = fields.map { fieldType =>
      fieldType.`type` match {
        case ColumnType.Date => DateTimeInput(_.source := fieldType.name, _.disabled := fieldType.disabled)
        case ColumnType.Text => TextInput(_.source := fieldType.name, _.disabled := fieldType.disabled)
        case ColumnType.Email => TextInput(_.source := fieldType.name, _.disabled := fieldType.disabled)
        case ColumnType.Reference(reference, source) =>
          ReferenceInput(_.source := fieldType.name, _.reference := reference)(
            SelectInput(_.optionText := source, _.disabled := fieldType.disabled)
          )
      }
    }
    Edit()(
      SimpleForm()(inputs: _*)
    )
  }
}
