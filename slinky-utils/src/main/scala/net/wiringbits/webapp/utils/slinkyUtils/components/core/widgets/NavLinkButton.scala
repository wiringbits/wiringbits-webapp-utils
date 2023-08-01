package net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets

import com.olvind.mui.csstype.mod.Property.TextAlign
import net.wiringbits.webapp.utils.slinkyUtils.facades.reactrouterdom.NavLink
import org.scalablytyped.runtime.StringDictionary
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.web.html.{className, onClick,style}
import com.olvind.mui.muiMaterial.components as mui
import com.olvind.mui.muiMaterial.mod.PropTypes.Color
import com.olvind.mui.muiMaterial.mod.makeStyles
import com.olvind.mui.muiMaterial.stylesCreateThemeMod.Theme
import com.olvind.mui.react.mod.CSSProperties
import com.olvind.mui.muiSystem.styleFunctionSxStyleFunctionSxMod.SystemCssProperties

import scala.scalajs.js

object NavLinkButton {
  case class Props(path: String, text: String, onClick: () => Unit)

  def apply(path: String, text: String, onClick: () => Unit): KeyAddingStage = {
    component(Props(path = path, text = text, onClick = onClick))
  }


// val newStyle = new SystemCssProperties[Theme]{
//      margin="0 8px"
//      padding="2px 4px"
//      color="inherit"
//      textAlign=TextAlign.inherit
//      textDecoration="none"
//  }
    val navLinkButtonStyle = js.Dynamic.literal(
      margin="0 8px",
      padding="2px 4px",
      color="inherit",
      textAlign=TextAlign.inherit,
      textDecoration="none"

    )
  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>

    val text = mui.Typography()(props.text).color(Color.inherit)

    NavLink(className := "navLinkButton",style:=navLinkButtonStyle, onClick := (_ => props.onClick()))(
      to = props.path,
      activeClassName = "navLinkButton navLinkButtonActive",
      exact = true
    )(text)
  }
}
