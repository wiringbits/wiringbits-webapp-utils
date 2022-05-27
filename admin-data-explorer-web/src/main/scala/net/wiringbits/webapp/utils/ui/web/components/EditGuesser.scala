package net.wiringbits.webapp.utils.ui.web.components

import japgolly.scalajs.react.vdom.html_<^._
import net.wiringbits.webapp.utils.api.models.AdminGetTables
import net.wiringbits.webapp.utils.ui.web.facades.reactadmin._
import net.wiringbits.webapp.utils.ui.web.models.{ColumnType, TableAction}
import net.wiringbits.webapp.utils.ui.web.utils.ResponseGuesser
import org.scalajs.dom

object EditGuesser {

  def apply(response: AdminGetTables.Response.DatabaseTable, tableActionsMaybe: Option[TableAction]) = {
    val fields = ResponseGuesser.getTypesFromResponse(response)
    val inputs: List[VdomNode] = fields
      .map { fieldType =>
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

    val buttons = tableActionsMaybe
      .map { tableActions =>
        tableActions.actions.map { action =>
          val primaryKey = dom.window.location.hash.split("/").lastOption.getOrElse("")
          Button(_.onClick := (() => action.onClick(primaryKey)))(action.text)
        }: List[VdomNode]
      }
      .getOrElse(List.empty)

    val actions: VdomNode = TopToolbar()(buttons: _*)

    Edit(_.actions := actions)(
      SimpleForm()(inputs: _*)
    )
  }
}
