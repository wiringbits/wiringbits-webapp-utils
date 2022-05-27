package net.wiringbits.webapp.utils.ui.web.components

import io.github.nafg.simplefacade.Factory
import japgolly.scalajs.react.vdom.html_<^._
import net.wiringbits.webapp.utils.api.models.AdminGetTables
import net.wiringbits.webapp.utils.ui.web.facades.reactadmin._
import net.wiringbits.webapp.utils.ui.web.models.ColumnType
import net.wiringbits.webapp.utils.ui.web.utils.ResponseGuesser

object ListGuesser {

  def apply(response: AdminGetTables.Response.DatabaseTable): Factory[ComponentList.Props] = {
    val fields = ResponseGuesser.getTypesFromResponse(response)
    val widgetFields: List[VdomNode] = fields.map { field =>
      field.`type` match {
        case ColumnType.Date => DateField(_.source := field.name, _.showTime := true)
        case ColumnType.Text => TextField(_.source := field.name)
        case ColumnType.Email => EmailField(_.source := field.name)
        case ColumnType.Reference(reference, source) =>
          ReferenceField(_.reference := reference, _.source := field.name)(TextField(_.source := source))
      }
    }
    ComponentList()(
      Datagrid(_.rowClick := "edit")(widgetFields: _*)
    )
  }
}
