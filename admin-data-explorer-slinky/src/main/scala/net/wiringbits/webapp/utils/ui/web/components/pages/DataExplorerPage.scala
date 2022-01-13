package net.wiringbits.webapp.utils.ui.web.components.pages

import net.wiringbits.webapp.utils.api.models.AdminGetTablesResponse
import net.wiringbits.webapp.utils.slinkyUtils.components.core.AsyncComponent
import net.wiringbits.webapp.utils.ui.web.API
import net.wiringbits.webapp.utils.ui.web.components.widgets.{ExperimentalTableListWidget, Loader}
import slinky.core.FunctionalComponent
import slinky.core.annotations.react

@react object DataExplorerPage {
  case class Props(api: API)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    AsyncComponent.component[AdminGetTablesResponse](
      AsyncComponent
        .Props(
          fetch = () => props.api.client.getTables(),
          render = response => ExperimentalTableListWidget.component(ExperimentalTableListWidget.Props(response)),
          progressIndicator = () => Loader()
        )
    )
  }
}
