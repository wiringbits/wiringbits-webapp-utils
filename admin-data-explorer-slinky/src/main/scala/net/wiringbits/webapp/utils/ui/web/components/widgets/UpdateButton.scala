package net.wiringbits.webapp.utils.ui.web.components.widgets

import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import com.alexitc.materialui.facade.materialUiIcons.{components => muiIcons}
import slinky.core.{FunctionalComponent, KeyAddingStage}

object UpdateButton {
  case class Props(onClick: () => Unit)

  def apply(onClick: () => Unit): KeyAddingStage = {
    component(Props(onClick = onClick))
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    mui.Tooltip(title = "Update")(
      mui
        .IconButton(
          mui.Icon(muiIcons.Edit())
        )
        .onClick(_ => props.onClick())
    )
  }
}
