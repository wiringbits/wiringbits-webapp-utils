package net.wiringbits.webapp.utils.ui.web.components.pages

import net.wiringbits.webapp.utils.ui.web.API
import net.wiringbits.webapp.utils.ui.web.components.widgets.ExperimentalTablesWidget
import slinky.core.FunctionalComponent
import slinky.core.annotations.react

@react object DataExplorerPage {
  case class Props(api: API)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    ExperimentalTablesWidget(props.api)
  }

}
