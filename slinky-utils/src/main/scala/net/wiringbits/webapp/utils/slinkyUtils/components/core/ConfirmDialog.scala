package net.wiringbits.webapp.utils.slinkyUtils.components.core

import com.olvind.mui.muiMaterial.components as mui
import slinky.core.{FunctionalComponent, KeyAddingStage}

object ConfirmDialog {

  case class Props(
      visible: Boolean,
      title: String,
      message: String,
      onConfirm: () => Unit,
      onCancel: () => Unit,
      confirmText: String = "Confirm",
      cancelText: String = "Cancel"
  )

  def apply(
      visible: Boolean,
      title: String,
      message: String,
      onConfirm: () => Unit,
      onCancel: () => Unit,
      confirmText: String = "Confirm",
      cancelText: String = "Cancel"
  ): KeyAddingStage = {
    component(
      Props(
        visible = visible,
        title = title,
        message = message,
        onConfirm = onConfirm,
        onCancel = onCancel,
        confirmText = confirmText,
        cancelText = cancelText
      )
    )
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    mui
      .Dialog(props.visible)
      .onClose((_, _) => props.onCancel())(
        mui.DialogTitle(props.title),
        mui.DialogContent(mui.DialogContentText(props.message)),
        mui.DialogActions(
          mui.Button.normal.color("secondary").onClick(_ => props.onCancel())(props.cancelText),
          mui.Button.normal
            .color("primary")
            .onClick(_ => props.onConfirm())(props.confirmText)
        )
      )
  }
}
