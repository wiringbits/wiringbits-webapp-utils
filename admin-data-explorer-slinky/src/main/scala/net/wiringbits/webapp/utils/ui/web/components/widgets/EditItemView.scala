package net.wiringbits.webapp.utils.ui.web.components.widgets

import net.wiringbits.webapp.utils.api.models.{AdminFindTable, AdminGetTableMetadata}
import slinky.core.facade.Hooks
import slinky.core.{FunctionalComponent, KeyAddingStage}

object EditItemView {
  case class Props(response: AdminFindTable.Response, onChange: AdminFindTable.Response => Unit)

  def apply(response: AdminFindTable.Response, onChange: AdminFindTable.Response => Unit): KeyAddingStage = {
    component(Props(response = response, onChange = onChange))
  }

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
