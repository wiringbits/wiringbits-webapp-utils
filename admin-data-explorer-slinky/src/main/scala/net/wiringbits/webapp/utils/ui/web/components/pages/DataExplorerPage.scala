package net.wiringbits.webapp.utils.ui.web.components.pages

import net.wiringbits.webapp.utils.api.models.AdminGetTables
import net.wiringbits.webapp.utils.slinkyUtils.components.core.AsyncComponent
import net.wiringbits.webapp.utils.ui.web.API
import net.wiringbits.webapp.utils.ui.web.components.widgets.{Loader, TableListWidget}
import slinky.core.FunctionalComponent
import slinky.core.KeyAddingStage

object DataExplorerPage {
  case class Props(api: API)

  def apply(api: API): KeyAddingStage = component(Props(api = api))

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    AsyncComponent.component[AdminGetTables.Response](
      AsyncComponent
        .Props(
          fetch = () => props.api.client.getTables,
          render = response => TableListWidget.component(TableListWidget.Props(response)),
          progressIndicator = () => Loader()
        )
    )
  }
}
