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

    val widgetFields: List[ReactElement] = fields.map { field =>
      val imageStyles = js.Dynamic.literal("width" -> "100px")
      val styles = js.Dynamic.literal("& img" -> imageStyles)
      field.`type` match {
        case ColumnType.Date => DateField(source = field.name, showTime = true)
        case ColumnType.Text => TextField(source = field.name)
        case ColumnType.Email => EmailField(source = field.name)
        case ColumnType.Image => ImageField(source = field.name, sx = styles)
        case ColumnType.Number => NumberField(source = field.name)
        case ColumnType.Reference(reference, source) =>
          ReferenceField(reference = reference, source = field.name)(
            TextField(source = source)
          )
      }
    }

    val filterList: List[ReactElement] = fields.filter(_.filterable).map { field =>
      field.`type` match {
        case ColumnType.Date => DateInput(source = field.name)
        case ColumnType.Text | ColumnType.Email => TextInput(source = field.name)
        case ColumnType.Image => Fragment()
        case ColumnType.Number => NumberInput(source = field.name)
        case ColumnType.Reference(reference, source) =>
          ReferenceField(reference = reference, source = field.name)(
            TextField(source = source)
          )
      }
    }

    val listToolbar: ReactElement = TopToolbar()(
      FilterButton(
        filters = filterList
      ),
      ExportButton()
    )

    ComponentList(actions = listToolbar, filters = filterList)(
      Datagrid(rowClick = "edit", bulkActionButtons = props.response.canBeDeleted)(widgetFields: _*)
    )
  }
}
