package net.wiringbits.webapp.utils.ui.web.components.widgets

import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import net.wiringbits.webapp.utils.api.models.AdminFindTable
import slinky.core.FunctionalComponent
import slinky.core.annotations.react

@react object RowView {
  case class Props(response: AdminFindTable.Response)
  // TODO: Check for empty value?

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val fieldNames = props.response.fields.map(_.name)
    val rowValues = props.response.row.data.map(_.value)
    // TODO: assert len of every list?

    val values = fieldNames.map(field => {
      val fieldValue = rowValues(fieldNames.indexOf(field))
      s"$field: $fieldValue"
    })

    mui.Typography(values)
  }
}
