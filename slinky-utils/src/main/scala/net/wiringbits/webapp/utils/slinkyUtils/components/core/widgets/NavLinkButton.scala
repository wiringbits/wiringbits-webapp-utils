package net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets

import com.olvind.mui.csstype.mod.Property.{TextAlign, TextDecorationStyle}
import com.olvind.mui.muiMaterial.components as mui
import com.olvind.mui.muiMaterial.stylesCreateTypographyMod.Variant
import com.olvind.mui.react.mod.CSSProperties
import net.wiringbits.webapp.utils.slinkyUtils.facades.reactrouterdom.NavLink
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.web.html.{onClick, style}

import scala.scalajs.js

object NavLinkButton {
  case class Props(path: String, text: String, onClick: () => Unit)

  def apply(path: String, text: String, onClick: () => Unit): KeyAddingStage = {
    component(Props(path = path, text = text, onClick = onClick))
  }

  private val navLinkButtonCss = new CSSProperties {
    margin = "0 8px"
    padding = "2px 4px"
    color = "inherit"
    textAlign = TextAlign.inherit
    textDecoration = TextDecorationStyle.inherit
  }

  private val navLinkButtonActiveCss = CSSProperties()

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val text = mui.Typography()(props.text).variant(Variant.h6).color("inherit")

    NavLink(onClick := (_ => props.onClick()), style := navLinkButtonCss)(
      to = props.path,
      activeStyle = navLinkButtonActiveCss,
      exact = true
    )(text)
  }
}
