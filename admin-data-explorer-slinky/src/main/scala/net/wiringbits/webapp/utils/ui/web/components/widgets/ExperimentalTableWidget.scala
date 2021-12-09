package net.wiringbits.webapp.utils.ui.web.components.widgets

import net.wiringbits.webapp.utils.api.models.AdminGetTableMetadataResponse
import net.wiringbits.webapp.utils.ui.components.core.AsyncComponent
import net.wiringbits.webapp.utils.ui.web.API
import org.scalajs.dom
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import typings.reactRouter.mod.useParams
import typings.std.global.URLSearchParams

import scala.scalajs.js
import scala.util.Try

@react object ExperimentalTableWidget {
  case class Props(api: API)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val defaultPageLength = 10
    val defaultOffset = 0

    val urlSearchParams = new URLSearchParams(dom.window.location.search)

    val limit = Try(urlSearchParams.get("limit").toString.toInt).toOption
    val offset = Try(urlSearchParams.get("offset").toString.toInt).toOption

    val tableName = useParams().asInstanceOf[js.Dynamic].tableName.toString

    AsyncComponent.component[AdminGetTableMetadataResponse](
      AsyncComponent
        .Props(
          fetch = () =>
            props.api.client
              .getTableMetadata(tableName, offset.getOrElse(defaultOffset), limit.getOrElse(defaultPageLength)),
          render = response => ExperimentalTable.component(ExperimentalTable.Props(response)),
          progressIndicator = () => Loader()
        )
    )
  }

}
