package net.wiringbits.webapp.utils.slinkyUtils.facades.reactrouterdom

import slinky.core.facade.ReactElement
import slinky.core.{ExternalComponentWithAttributes, TagMod}
import slinky.web.html.a

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

@JSImport("react-router-dom", "NavLink")
@js.native
private object NavLinkComponent extends js.Object

object NavLink extends ExternalComponentWithAttributes[a.tag.type] {
  case class Props(
      to: String,
      activeClassName: js.UndefOr[String] = js.undefined,
      exact: Boolean = false,
      children: ReactElement
  )
  override val component: String | js.Object = NavLinkComponent

  def apply(mods: TagMod[a.tag.type]*)(
      to: String,
      activeClassName: js.UndefOr[String] = js.undefined,
      exact: Boolean = false
  )(children: ReactElement): ReactElement = {
    apply(Props(to = to, activeClassName = activeClassName, exact = exact, children = children))(mods: _*)
  }
}
