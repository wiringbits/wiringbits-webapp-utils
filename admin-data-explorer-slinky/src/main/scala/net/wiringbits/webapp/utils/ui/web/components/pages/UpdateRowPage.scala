package net.wiringbits.webapp.utils.ui.web.components.pages

import net.wiringbits.facades.reactRouter.mod.useParams
import net.wiringbits.webapp.utils.api.models.AdminFindTable
import net.wiringbits.webapp.utils.slinkyUtils.components.core.AsyncComponent
import net.wiringbits.webapp.utils.ui.web.API
import net.wiringbits.webapp.utils.ui.web.components.widgets.{Loader, UpdateView}
import slinky.core.FunctionalComponent
import slinky.core.annotations.react

import scala.scalajs.js

@react object UpdateRowPage {
  case class Props(api: API)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val tableName = useParams().asInstanceOf[js.Dynamic].tableName.toString
    val ID = useParams().asInstanceOf[js.Dynamic].ID.toString

    UpdateView(props.api, tableName, ID)
  }
}
