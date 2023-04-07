package net.wiringbits.webapp.utils.slinkyUtils.facades.reactrouterdom

import slinky.core.ExternalComponent
import slinky.core.facade.ReactElement

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

@JSImport("react-router-dom", "Redirect")
@js.native
private object RedirectComponent extends js.Object

object Redirect extends ExternalComponent {
  case class Props(to: String)
  override val component: String | js.Object = RedirectComponent

  def apply(to: String): ReactElement = {
    super.apply(Props(to = to))
  }
}
