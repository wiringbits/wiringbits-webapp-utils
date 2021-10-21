package net.wiringbits.webapp.utils.web.components.pages

import net.wiringbits.webapp.utils.web.API
import net.wiringbits.webapp.utils.web.components.widgets.ExperimentalTableWidget
import slinky.core.FunctionalComponent
import slinky.core.annotations.react

@react object ExperimentalTablesPage {
  case class Props(api: API)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    ExperimentalTableWidget(props.api)
  }

}
