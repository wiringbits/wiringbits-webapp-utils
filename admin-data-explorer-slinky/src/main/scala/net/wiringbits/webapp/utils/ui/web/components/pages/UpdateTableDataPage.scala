package net.wiringbits.webapp.utils.ui.web.components.pages

import net.wiringbits.webapp.utils.ui.web.API
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment

@react object UpdateTableDataPage {
  case class Props(api: API)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { _ => Fragment() }
}
