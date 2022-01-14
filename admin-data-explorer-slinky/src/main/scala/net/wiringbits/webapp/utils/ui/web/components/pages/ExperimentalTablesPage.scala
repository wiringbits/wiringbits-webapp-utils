package net.wiringbits.webapp.utils.ui.web.components.pages

import net.wiringbits.webapp.utils.ui.web.API
import net.wiringbits.webapp.utils.ui.web.components.widgets.ExperimentalTableWidget
import slinky.core.{FunctionalComponent, KeyAddingStage}

object ExperimentalTablesPage {
  case class Props(api: API)

  def apply(api: API): KeyAddingStage = component(Props(api = api))

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    ExperimentalTableWidget(props.api)
  }
}
