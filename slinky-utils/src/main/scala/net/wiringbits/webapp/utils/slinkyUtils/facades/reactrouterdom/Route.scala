package net.wiringbits.webapp.utils.slinkyUtils.facades.reactrouterdom

import slinky.core.ExternalComponent
import slinky.core.facade.ReactElement

import scala.scalajs.js
import scala.scalajs.js.|

object Route extends ExternalComponent {
  case class Props(
      path: String,
      exact: Boolean,
      children: ReactElement
  )
  override val component: String | js.Object = ReactRouterDom.Route

  def apply(path: String, exact: Boolean = false, children: ReactElement): ReactElement = {
    super.apply(Props(path = path, exact = exact, children = children))
  }
}
