package net.wiringbits.webapp.utils.ui.web.components.widgets

import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import net.wiringbits.webapp.utils.api.models.AdminGetTableMetadata
import slinky.core.FunctionalComponent
import slinky.core.annotations.react

@react object TableRow {
  case class Props(response: AdminGetTableMetadata.Response.TableRow, tableName: String)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val row = props.response.data

    mui
      .TableRow(
        row.map { cell =>
          // TODO: Here I'm asumming the first column is ID
          if (row.indexOf(cell) == 0)
            TableNavCell(cell.value, props.tableName)
          else
            TableCell(cell.value, props.tableName)
        }
      )
  }
}
