package net.wiringbits.webapp.utils.ui.web.facades.reactadmin

import net.wiringbits.webapp.utils.ui.web.facades.DataProvider
import slinky.core.ExternalComponent
import slinky.core.facade.ReactElement

import scala.scalajs.js
import scala.scalajs.js.|

object Admin extends ExternalComponent {
  case class Props(dataProvider: DataProvider, children: Seq[ReactElement])
  override val component: String | js.Object = ReactAdmin.Admin
}
