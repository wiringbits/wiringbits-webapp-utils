package net.wiringbits.webapp.utils.ui.web.components

import io.github.nafg.simplefacade.Factory
import japgolly.scalajs.react.vdom.all.div
import japgolly.scalajs.react.vdom.html_<^._
import net.wiringbits.webapp.utils.api.models.AdminGetTables
import net.wiringbits.webapp.utils.ui.web.facades.reactadmin._
import net.wiringbits.webapp.utils.ui.web.models.ColumnType
import net.wiringbits.webapp.utils.ui.web.utils.ResponseGuesser

import scala.scalajs.js

object ListGuesser {
  def apply(response: AdminGetTables.Response.DatabaseTable): Factory[ComponentList.Props] = {
    val fields = ResponseGuesser.getTypesFromResponse(response)

    val widgetFields: List[VdomNode] = fields.map { field =>
      val imageStyles = js.Dynamic.literal("width" -> "100px")
      val styles = js.Dynamic.literal("& img" -> imageStyles)
      field.`type` match {
        case ColumnType.Date => DateField(_.source := field.name, _.showTime := true)
        case ColumnType.Text => TextField(_.source := field.name)
        case ColumnType.Email => EmailField(_.source := field.name)
        case ColumnType.Image => ImageField(_.source := field.name, _.sx := styles)
        case ColumnType.Number => NumberField(_.source := field.name)
        case ColumnType.Reference(reference, source) =>
          ReferenceField(_.reference := reference, _.source := field.name)(
            TextField(_.source := source)
          )
      }
    }

    val filterList: List[VdomNode] = fields.filter(_.filterable).map { field =>
      field.`type` match {
        case ColumnType.Date => DateInput(_.source := field.name)
        case ColumnType.Text | ColumnType.Email => TextInput(_.source := field.name)
        case ColumnType.Image => div()
        case ColumnType.Number => NumberInput(_.source := field.name)
        case ColumnType.Reference(reference, source) =>
          ReferenceField(_.reference := reference, _.source := field.name)(
            TextField(_.source := source)
          )
      }
    }

    val listToolbar: VdomNode = TopToolbar()(
      FilterButton(
        _.filters := filterList
      ),
      ExportButton()
    )

    ComponentList(_.actions := listToolbar, _.filters := filterList)(
      Datagrid(_.rowClick := "edit", _.bulkActionButtons := response.canBeDeleted)(widgetFields: _*)
    )
  }
}
