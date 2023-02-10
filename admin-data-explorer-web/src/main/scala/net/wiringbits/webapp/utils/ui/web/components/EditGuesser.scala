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
    val inputs: Seq[ReactElement] = fields.map { field =>
      field.`type` match {
        case ColumnType.Date => DateTimeInput(DateTimeInput.Props(source = field.name, disabled = field.disabled))
        case ColumnType.Text => TextInput(TextInput.Props(source = field.name, disabled = field.disabled))
        case ColumnType.Email => TextInput(TextInput.Props(source = field.name, disabled = field.disabled))
        case ColumnType.Image => ImageField(ImageField.Props(source = field.name))
        case ColumnType.Number => NumberInput(NumberInput.Props(source = field.name, disabled = field.disabled))
        case ColumnType.Reference(reference, source) =>
          ReferenceInput(
            ReferenceInput.Props(
              source = field.name,
              reference = reference,
              children = Seq(SelectInput(SelectInput.Props(optionText = source, disabled = field.disabled)))
            )
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

    def buttons(): Seq[ReactElement] = {
      val ctx = useEditContext()
      tableAction
        .map { x =>
          x.actions.map { action =>
            Button(Button.Props(onClick = () => onClick(action, ctx), children = Seq(action.text)))
          }: Seq[ReactElement]
        }
        .getOrElse(Seq.empty)
    }

    val actions = TopToolbar(TopToolbar.Props(children = buttons()))

    val deleteButton: ReactElement = if (props.response.canBeDeleted) DeleteButton() else Fragment()
    val toolbar: ReactElement = Toolbar(
      Toolbar.Props(children =
        Seq(
          SaveButton(),
          deleteButton
        )
      )
    )

    Edit(
      Edit.Props(
        actions = actions(),
        children = Seq(SimpleForm(SimpleForm.Props(toolbar = toolbar, children = inputs)))
      )
    )
  }
}
