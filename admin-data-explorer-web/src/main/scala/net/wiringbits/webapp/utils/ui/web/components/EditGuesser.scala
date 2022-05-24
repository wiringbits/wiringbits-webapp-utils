package net.wiringbits.webapp.utils.ui.web.components

import japgolly.scalajs.react.vdom.html_<^.VdomNode
import net.wiringbits.webapp.utils.api.models.AdminGetTables
import net.wiringbits.webapp.utils.ui.web.facades.reactadmin._
import net.wiringbits.webapp.utils.ui.web.models.FieldType
import net.wiringbits.webapp.utils.ui.web.utils.ResponseGuesser

object EditGuesser {

  def apply(response: AdminGetTables.Response.DatabaseTable) = {
    val fields = ResponseGuesser.getTypesFromResponse(response)
    val inputs: List[VdomNode] = fields.map { fieldType =>
      fieldType.`type` match {
        case FieldType.Date => DateTimeInput(_.source := fieldType.name, _.disabled := fieldType.disabled)
        case FieldType.Text => TextInput(_.source := fieldType.name, _.disabled := fieldType.disabled)
        case FieldType.Email => TextInput(_.source := fieldType.name, _.disabled := fieldType.disabled)
        // TODO: disable to reference?
        case FieldType.Reference(reference) =>
          ReferenceInput(_.source := fieldType.name, _.reference := reference)(SelectInput(_.optionText := "id"))
      }
    }
    Edit()(
      SimpleForm()(inputs: _*)
    )
  }
}
