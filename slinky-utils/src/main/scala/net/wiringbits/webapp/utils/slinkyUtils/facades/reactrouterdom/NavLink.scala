package net.wiringbits.webapp.utils.slinkyUtils.facades.reactrouterdom

import com.olvind.mui.react.mod.CSSProperties
import slinky.core.facade.ReactElement
import slinky.core.{ExternalComponentWithAttributes, TagMod}
import slinky.web.html.a

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport("react-router-dom", "NavLink")
@js.native
private object NavLinkComponent extends js.Object

object NavLink extends ExternalComponentWithAttributes[a.tag.type] {
  case class Props(
      to: String,
      activeStyle: js.UndefOr[CSSProperties] = js.undefined,
      exact: Boolean,
      children: ReactElement
  )
  override val component: String | js.Object = NavLinkComponent

  def apply(mods: TagMod[a.tag.type]*)(
      to: String,
      activeStyle: js.UndefOr[CSSProperties] = js.undefined,
      exact: Boolean = false
  )(children: ReactElement): ReactElement = {
    apply(Props(to = to, activeStyle = activeStyle, exact = exact, children = children))(mods: _*)
  }
}
