package net.wiringbits.webapp.utils.slinkyUtils.facades.reactrouterdom

import slinky.core.ExternalComponent
import slinky.core.facade.ReactElement

import scala.scalajs.js
import scala.scalajs.js.|

object Redirect extends ExternalComponent {
  case class Props(to: String)
  override val component: String | js.Object = ReactRouterDom.Redirect

  def apply(to: String): ReactElement = {
    super.apply(Props(to = to))
  }
}
