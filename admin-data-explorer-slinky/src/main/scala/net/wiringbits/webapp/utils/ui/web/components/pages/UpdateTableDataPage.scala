package net.wiringbits.webapp.utils.ui.web.components.pages

import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import net.wiringbits.webapp.utils.ui.web.API
import slinky.core.FunctionalComponent
import slinky.core.annotations.react

@react object UpdateTableDataPage {
  case class Props(api: API)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    mui.Table()
  }
}
