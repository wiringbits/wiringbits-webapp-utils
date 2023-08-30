package net.wiringbits.webapp.utils.slinkyUtils.components.core

import com.olvind.mui.muiMaterial.components as mui
import slinky.core.{FunctionalComponent, KeyAddingStage}

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
      .onClose((_, _) => props.onClose())(
        mui.DialogTitle(props.title),
        mui.DialogContent(mui.DialogContentText(props.message)),
        mui.DialogActions(
          mui.Button.normal
            .variant("contained")
            .color("primary")
            .onClick(_ => props.onClose())(props.closeText)
        )
      )
  }
}
