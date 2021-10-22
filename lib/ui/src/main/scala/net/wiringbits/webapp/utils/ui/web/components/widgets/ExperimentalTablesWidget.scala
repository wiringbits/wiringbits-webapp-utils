package net.wiringbits.webapp.utils.ui.web.components.widgets

import net.wiringbits.webapp.utils.api.models.AdminGetTablesResponse
import net.wiringbits.webapp.utils.ui.components.core.RemoteDataLoader
import net.wiringbits.webapp.utils.ui.web.API
import slinky.core.FunctionalComponent
import slinky.core.annotations.react

@react object ExperimentalTablesWidget {
  case class Props(api: API)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    RemoteDataLoader.component[AdminGetTablesResponse](
      RemoteDataLoader
        .Props(
          fetch = () => props.api.client.adminGetTables(),
          render = response => ExperimentalTableListWidget.component(ExperimentalTableListWidget.Props(response)),
          progressIndicator = () => Loader()
        )
    )
  }

}
