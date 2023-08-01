package net.wiringbits.webapp.utils.slinkyUtils.components.core

import org.scalablytyped.runtime.StringDictionary
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.web.html.{className, div, h1}
import com.olvind.mui.muiIconsMaterial.components as muiIcons
import com.olvind.mui.muiMaterial.mod.makeStyles
import com.olvind.mui.muiMaterial.stylesCreateThemeMod.Theme
import com.olvind.mui.react.mod.CSSProperties
import com.olvind.mui.csstype.mod.Property.FlexDirection
import com.olvind.mui.muiSystem.styleFunctionSxStyleFunctionSxMod.SystemCssProperties

object ErrorBoundaryInfo {
  case class Props(error: scala.scalajs.js.Error)

  def apply(error: scala.scalajs.js.Error): KeyAddingStage = {
    component(Props(error))
  }


  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme:Theme =>
      StringDictionary(
        "errorBoundaryInfo" -> CSSProperties()
          .setFlex("auto")
          .setDisplay("flex")
          .setFlexDirection(FlexDirection.column)
          .setAlignItems("center")
          .setJustifyContent("center"),
        "content" -> CSSProperties()
          .setDisplay("flex")
          .setFlexDirection(FlexDirection.column),
        "icon" -> CSSProperties()
          .setDisplay("flex")
          .setJustifyContent("center")
          .set(
            "& svg ",
            CSSProperties()
              .setFontSize("4em")
          )
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }
    val errorBoundaryInfoStyle = new SystemCssProperties[Theme]{
     display="flex"
     flexDirection=FlexDirection.column
     alignItems="center"
     justifyContent="center"
  }
    val contentStyle = new SystemCssProperties[Theme]{
     display="flex"
     flexDirection=FlexDirection.column

  }    
    val iconStyle = new SystemCssProperties[Theme]{
     display="flex"
     justifyContent="center"
  }

  
  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val e = props.error

    div(
      className := "errorBoundaryInfo",
      div(
        className := "content",
        div(className := "icon", muiIcons.Warning()),
        h1("You hit an unexpected error"),
        div(e.toString)
      )
    )
  }
}
