package net.wiringbits.webapp.utils.ui.web.components.pages

import net.wiringbits.webapp.utils.ui.web.API
import net.wiringbits.webapp.utils.ui.web.components.widgets.ExperimentalTablesWidget
import slinky.core.{FunctionalComponent, KeyAddingStage}

object DataExplorerPage {
  case class Props(api: API)

  def apply(api: API): KeyAddingStage = component(Props(api = api))

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    ExperimentalTablesWidget(props.api)
  }

}
