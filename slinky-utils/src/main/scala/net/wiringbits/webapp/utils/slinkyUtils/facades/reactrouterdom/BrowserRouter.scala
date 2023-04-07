package net.wiringbits.webapp.utils.slinkyUtils.facades.reactrouterdom

import slinky.core.ExternalComponent
import slinky.core.facade.ReactElement

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

@JSImport("react-router-dom", "BrowserRouter")
@js.native
private object BrowserRouterComponent extends js.Object

object BrowserRouter extends ExternalComponent {
  case class Props(basename: String, children: ReactElement)
  override val component: String | js.Object = BrowserRouterComponent

  def apply(basename: String, children: ReactElement): ReactElement = {
    super.apply(Props(basename = basename, children = children))
  }
}
