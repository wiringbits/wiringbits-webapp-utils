package net.wiringbits.webapp.utils.ui.web.components.widgets

import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import com.alexitc.materialui.facade.materialUiIcons.{components => muiIcons}
import slinky.core.FunctionalComponent
import slinky.core.annotations.react

@react object DeleteButton {
  case class Props(onClick: () => Unit)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    mui.Tooltip(title = "Delete")(
      mui
        .IconButton(
          mui.Icon(muiIcons.Delete())
        )
        .onClick(_ => props.onClick())
    )
  }
}
