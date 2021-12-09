package net.wiringbits.webapp.utils.slinkyUtils.components.core

import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import slinky.core.FunctionalComponent
import slinky.core.annotations.react

@react object AlertDialog {
  case class Props(visible: Boolean, title: String, message: String, onClose: () => Unit, closeText: String = "Close")

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    mui
      .Dialog(props.visible)
      .onClose(_ => props.onClose())(
        mui.DialogTitle(props.title),
        mui.DialogContent(mui.DialogContentText(props.message)),
        mui.DialogActions(
          mui
            .Button()
            .variant(muiStrings.contained)
            .color(muiStrings.primary)
            .onClick(_ => props.onClose())(props.closeText)
        )
      )
  }
}
