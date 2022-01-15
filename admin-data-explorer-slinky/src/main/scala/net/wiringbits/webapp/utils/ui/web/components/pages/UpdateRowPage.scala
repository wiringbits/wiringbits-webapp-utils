package net.wiringbits.webapp.utils.ui.web.components.pages

import net.wiringbits.facades.reactRouter.mod.useParams
import net.wiringbits.webapp.utils.ui.web.API
import net.wiringbits.webapp.utils.ui.web.components.widgets.UpdateView
import slinky.core.{FunctionalComponent, KeyAddingStage}

import scala.scalajs.js

object UpdateRowPage {
  case class Props(api: API)

  def apply(api: API): KeyAddingStage = {
    component(Props(api = api))
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val tableName = useParams().asInstanceOf[js.Dynamic].tableName.toString
    val ID = useParams().asInstanceOf[js.Dynamic].ID.toString

    UpdateView(props.api, tableName, ID)
  }
}
