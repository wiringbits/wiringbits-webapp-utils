package net.wiringbits.webapp.utils.ui.web.components

import net.wiringbits.webapp.utils.api.models.AdminGetTables
import net.wiringbits.webapp.utils.ui.web.facades.reactadmin._
import net.wiringbits.webapp.utils.ui.web.models.ColumnType
import net.wiringbits.webapp.utils.ui.web.utils.ResponseGuesser
import slinky.core.facade.{Fragment, ReactElement}
import slinky.core.{FunctionalComponent, KeyAddingStage}

import scala.scalajs.js

object ListGuesser {
  case class Props(response: AdminGetTables.Response.DatabaseTable)

  def apply(response: AdminGetTables.Response.DatabaseTable): KeyAddingStage = {
    component(Props(response))
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val fields = ResponseGuesser.getTypesFromResponse(props.response)

    def defaultField(reference: String, source: String, children: Seq[ReactElement]): ReactElement =
      ReferenceField(
        ReferenceField.Props(
          reference = reference,
          source = source,
          children = children
        )
      )

    val widgetFields: Seq[ReactElement] = fields.map { field =>
      val imageStyles = js.Dynamic.literal("width" -> "100px")
      val styles = js.Dynamic.literal("& img" -> imageStyles)
      field.`type` match {
        case ColumnType.Date => DateField(DateField.Props(source = field.name, showTime = true))
        case ColumnType.Text => TextField(TextField.Props(source = field.name))
        case ColumnType.Email => EmailField(EmailField.Props(source = field.name))
        case ColumnType.Image => ImageField(ImageField.Props(source = field.name, sx = styles))
        case ColumnType.Number => NumberField(NumberField.Props(source = field.name))
        case ColumnType.Reference(reference, source) =>
          defaultField(reference, field.name, Seq(TextField(TextField.Props(source = source))))
      }
    }

    val filterList: Seq[ReactElement] = fields.filter(_.filterable).map { field =>
      field.`type` match {
        case ColumnType.Date => DateInput(DateInput.Props(source = field.name))
        case ColumnType.Text | ColumnType.Email => TextInput(TextInput.Props(source = field.name))
        case ColumnType.Image => Fragment()
        case ColumnType.Number => NumberInput(NumberInput.Props(source = field.name))
        case ColumnType.Reference(reference, source) =>
          defaultField(reference, field.name, Seq(TextField(TextField.Props(source = source))))
      }
    }

    val listToolbar: ReactElement = TopToolbar(
      TopToolbar.Props(
        children = Seq(
          FilterButton(FilterButton.Props(filters = filterList)),
          ExportButton()
        )
      )
    )

    ComponentList(ComponentList.Props(actions = listToolbar, filters = filterList))(
      Datagrid(
        Datagrid.Props(rowClick = "edit", bulkActionButtons = props.response.canBeDeleted, children = widgetFields)
      )
    )
  }
}
