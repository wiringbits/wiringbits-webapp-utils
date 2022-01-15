package net.wiringbits.webapp.utils.ui.web.components.widgets

import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import org.scalajs.dom
import slinky.core.{FunctionalComponent, KeyAddingStage}

object CellInput {
  case class Props(label: String, initialValue: String, onChange: String => Unit, disabled: Boolean = false)

  def apply(
      label: String,
      initialValue: String,
      onChange: String => Unit,
      disabled: Boolean = false
  ): KeyAddingStage = {
    component(
      Props(
        label = label,
        initialValue = initialValue,
        onChange = onChange,
        disabled = disabled
      )
    )
  }
  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    mui.TextField
      .OutlinedTextFieldProps()
      .name(props.label)
      .label(props.label)
      .placeholder(props.label)
      .margin(muiStrings.dense)
      .fullWidth(true)
      .value(props.initialValue)
      .onChange(evt => props.onChange(evt.target.asInstanceOf[dom.HTMLInputElement].value))
      .disabled(props.disabled)
  }
}
