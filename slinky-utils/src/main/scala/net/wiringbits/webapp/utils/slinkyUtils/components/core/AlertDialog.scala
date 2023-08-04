package net.wiringbits.webapp.utils.slinkyUtils.components.core

import slinky.core.{FunctionalComponent, KeyAddingStage}
import com.olvind.mui.muiMaterial.components as mui

object AlertDialog {
  case class Props(visible: Boolean, title: String, message: String, onClose: () => Unit, closeText: String = "Close")

  def apply(
      visible: Boolean,
      title: String,
      message: String,
      onClose: () => Unit,
      closeText: String = "Close"
  ): KeyAddingStage = {
    component(Props(visible = visible, title = title, message = message, onClose = onClose, closeText = closeText))
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    mui
      .Dialog(props.visible)
      .onClose((a,b) => props.onClose())(
        mui.DialogTitle(props.title),
        mui.DialogContent(mui.DialogContentText(props.message)),
        mui.DialogActions(
          mui.Button.normal()
            .onClick(_ => props.onClose())(props.closeText)
        )
      )
  }
}
