package net.wiringbits.webapp.utils.slinkyUtils.facades.reactrouterdom

import slinky.core.ExternalComponent
import slinky.core.facade.ReactElement

import scala.scalajs.js
import scala.scalajs.js.|

object BrowserRouter extends ExternalComponent {
  case class Props(basename: String, children: ReactElement)
  override val component: String | js.Object = ReactRouterDom.BrowserRouter

  def apply(basename: String, children: ReactElement): ReactElement = {
    super.apply(Props(basename = basename, children = children))
  }
}
