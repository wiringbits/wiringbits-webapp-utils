package net.wiringbits.webapp.utils.ui.web.components

import japgolly.scalajs.react.ScalaFnComponent
import japgolly.scalajs.react.vdom.html_<^._
import net.wiringbits.webapp.utils.api.models.AdminGetTables
import net.wiringbits.webapp.utils.ui.web.facades.reactadmin.ReactAdmin.useEditContext
import net.wiringbits.webapp.utils.ui.web.facades.reactadmin._
import net.wiringbits.webapp.utils.ui.web.models.{ButtonAction, ColumnType, TableAction}
import net.wiringbits.webapp.utils.ui.web.utils.ResponseGuesser
import org.scalajs.dom
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.global

import scala.scalajs.js
import scala.util.{Failure, Success}

object EditGuesser {

  def apply(response: AdminGetTables.Response.DatabaseTable, tableAction: Option[TableAction]) = {
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

    def onClick(action: ButtonAction, ctx: js.Dictionary[js.Any]): Unit = {
      val primaryKey = dom.window.location.hash.split("/").lastOption.getOrElse("")
      action.onClick(primaryKey).onComplete {
        case Failure(ex) => ex.printStackTrace()
        case Success(_) => refetch(ctx)
      }
    }

    def refetch(ctx: js.Dictionary[js.Any]): Unit = {
      val _ = ctx.get("refetch").map(_.asInstanceOf[js.Dynamic].apply())
    }

    def buttons(ctx: js.Dictionary[js.Any]) = tableAction
      .map { x =>
        x.actions.map { action =>
          Button(_.onClick := (() => onClick(action, ctx)))(action.text)
        }: List[VdomNode]
      }
      .getOrElse(List.empty)

    val actions = ScalaFnComponent.withHooks[Unit].unchecked(useEditContext()).render { (_, ctx) =>
      {
        TopToolbar()(buttons(ctx): _*)
      }: VdomNode
    }

    Edit(_.actions := actions())(
      SimpleForm()(inputs: _*)
    )
  }
}
