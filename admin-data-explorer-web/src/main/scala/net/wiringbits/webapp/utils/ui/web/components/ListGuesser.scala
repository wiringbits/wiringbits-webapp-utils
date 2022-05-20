package net.wiringbits.webapp.utils.ui.web.components

import io.github.nafg.simplefacade.Factory
import japgolly.scalajs.react.vdom.html_<^._
import net.wiringbits.webapp.utils.api.models.AdminGetTables
import net.wiringbits.webapp.utils.ui.web.facades.reactadmin._
import net.wiringbits.webapp.utils.ui.web.models.FieldType
import net.wiringbits.webapp.utils.ui.web.utils.ResponseGuesser

object ListGuesser {

  def apply(response: AdminGetTables.Response.DatabaseTable): Factory[List.Props] = {
    val fields = ResponseGuesser.getTypesFromResponse(response)
    val widgetFields: List[VdomNode] = fields.map { field =>
      field.`type` match {
        case FieldType.Date => DateField(_.source := field.name)
        case FieldType.Text => TextField(_.source := field.name)
        case FieldType.Email => EmailField(_.source := field.name)
      }
    }
    List()(
      Datagrid(_.rowClick := "edit")(widgetFields: _*)
    )
  }
}
