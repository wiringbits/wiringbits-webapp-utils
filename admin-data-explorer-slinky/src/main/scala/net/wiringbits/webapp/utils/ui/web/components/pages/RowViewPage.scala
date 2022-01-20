package net.wiringbits.webapp.utils.ui.web.components.pages

import net.wiringbits.facades.reactRouter.mod.useParams
import net.wiringbits.webapp.utils.api.models.AdminFindTable
import net.wiringbits.webapp.utils.slinkyUtils.components.core.AsyncComponent
import net.wiringbits.webapp.utils.ui.web.API
import net.wiringbits.webapp.utils.ui.web.components.widgets.{Loader, RowView}
import slinky.core.{FunctionalComponent, KeyAddingStage}

import scala.scalajs.js

object RowViewPage {
  case class Props(api: API)

  def apply(api: API): KeyAddingStage = {
    component(Props(api = api))
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val tableName = useParams().asInstanceOf[js.Dynamic].tableName.toString
    val ID = useParams().asInstanceOf[js.Dynamic].ID.toString

    AsyncComponent.component[AdminFindTable.Response](
      AsyncComponent
        .Props(
          fetch = () => props.api.client.viewItem(tableName, ID),
          render = response => RowView.component(RowView.Props(props.api, response, tableName, ID)),
          progressIndicator = () => Loader()
        )
    )
  }

}
