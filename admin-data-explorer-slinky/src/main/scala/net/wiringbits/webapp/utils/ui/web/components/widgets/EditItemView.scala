package net.wiringbits.webapp.utils.ui.web.components.widgets

import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import net.wiringbits.webapp.utils.api.models.{AdminFindTable, AdminGetTableMetadata}
import org.scalajs.dom
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Hooks
import slinky.web.html.div

import scala.collection.mutable.ListBuffer

@react object EditItemView {
  case class Props(response: AdminFindTable.Response, onChange: AdminFindTable.Response => Unit)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val (formData, setFormData) = Hooks.useState(props.response)

    def customInput(name: String, value: String, onChange: String => Unit, editable: Boolean = true) = mui.TextField
      .OutlinedTextFieldProps()
      .name(name)
      .label(name)
      .placeholder(name)
      .margin(muiStrings.dense)
      .fullWidth(true)
      .value(value)
      .onChange(evt => onChange(evt.target.asInstanceOf[dom.HTMLInputElement].value))
      .disabled(!editable)

    def onChange(value: AdminFindTable.Response): Unit = {
      setFormData(value)
      props.onChange(value)
    }

    div()(
      props.response.row.data.map { cell =>
        val currentIndex = props.response.row.data.indexOf(cell)
        val fieldName = props.response.fields(currentIndex).name
        customInput(
          fieldName,
          cell.value,
          value => {
            val newCell = AdminGetTableMetadata.Response.Cell(value)
            val rowData = props.response.row.data.updated(currentIndex, newCell)
            onChange(formData.copy(row = AdminGetTableMetadata.Response.TableRow(rowData)))
          }
        )
      }
    )
  }
}
