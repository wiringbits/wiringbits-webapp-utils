package net.wiringbits.webapp.utils.ui.web.components

import net.wiringbits.webapp.utils.api.models.AdminGetTables
import net.wiringbits.webapp.utils.ui.web.facades.reactadmin.ReactAdmin.useEditContext
import net.wiringbits.webapp.utils.ui.web.facades.reactadmin._
import net.wiringbits.webapp.utils.ui.web.models.{ButtonAction, ColumnType, DataExplorerSettings}
import net.wiringbits.webapp.utils.ui.web.utils.ResponseGuesser
import org.scalajs.dom
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.global
import slinky.core.facade.{Fragment, ReactElement}
import slinky.core.{FunctionalComponent, KeyAddingStage}

import scala.scalajs.js
import scala.util.{Failure, Success}

object EditGuesser {
  case class Props(response: AdminGetTables.Response.DatabaseTable, dataExplorerSettings: DataExplorerSettings)

  def apply(
      response: AdminGetTables.Response.DatabaseTable,
      dataExplorerSettings: DataExplorerSettings
  ): KeyAddingStage = {
    component(Props(response, dataExplorerSettings))
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val fields = ResponseGuesser.getTypesFromResponse(props.response)
    val inputs: List[ReactElement] = fields.map { field =>
      field.`type` match {
        case ColumnType.Date => DateTimeInput(source = field.name, disabled = field.disabled)
        case ColumnType.Text => TextInput(source = field.name, disabled = field.disabled)
        case ColumnType.Email => TextInput(source = field.name, disabled = field.disabled)
        case ColumnType.Image => ImageField(source = field.name)
        case ColumnType.Number => NumberInput(source = field.name, disabled = field.disabled)
        case ColumnType.Reference(reference, source) =>
          ReferenceInput(source = field.name, reference = reference)(
            SelectInput(optionText = source, disabled = field.disabled)
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

    val tableAction = props.dataExplorerSettings.actions.find(_.tableName == props.response.name)

    def buttons(): List[ReactElement] = {
      val ctx = useEditContext()
      tableAction
        .map { x =>
          x.actions.map { action =>
            Button(onClick = () => onClick(action, ctx))(action.text)
          }: List[ReactElement]
        }
        .getOrElse(List.empty)
    }

    val actions = TopToolbar()(buttons(): _*)

    val deleteButton: ReactElement = if (props.response.canBeDeleted) DeleteButton() else Fragment()
    val toolbar: ReactElement = Toolbar()(
      SaveButton(),
      deleteButton
    )

    Edit(actions = actions())(
      SimpleForm(toolbar = toolbar)(inputs: _*)
    )
  }
}
