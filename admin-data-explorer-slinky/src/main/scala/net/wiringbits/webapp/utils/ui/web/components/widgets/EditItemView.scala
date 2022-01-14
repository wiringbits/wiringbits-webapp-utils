package net.wiringbits.webapp.utils.ui.web.components.widgets

import net.wiringbits.webapp.utils.api.models.{AdminFindTable, AdminGetTableMetadata}
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Hooks

@react object EditItemView {
  case class Props(response: AdminFindTable.Response, onChange: AdminFindTable.Response => Unit)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val (formData, setFormData) = Hooks.useState(props.response)

    def onChange(value: AdminFindTable.Response): Unit = {
      setFormData(value)
      props.onChange(value)
    }

    props.response.row.data.map { cell =>
      val currentIndex = props.response.row.data.indexOf(cell)
      val fieldName = props.response.fields(currentIndex).name
      CellInput(
        label = fieldName,
        initialValue = cell.value,
        onChange = value => {
          val newCell = AdminGetTableMetadata.Response.Cell(value)
          val rowData = props.response.row.data.updated(currentIndex, newCell)
          onChange(formData.copy(row = AdminGetTableMetadata.Response.TableRow(rowData)))
        }
      )
    }
  }
}
