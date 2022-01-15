package net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets

import com.alexitc.materialui.facade.csstype.mod.TextAlignProperty
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes.Color
import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import net.wiringbits.facades.reactRouterDom.{components => router}
import org.scalablytyped.runtime.StringDictionary
import slinky.core.{FunctionalComponent, KeyAddingStage}

object NavLinkButton {
  case class Props(path: String, text: String, onClick: () => Unit)

  def apply(path: String, text: String, onClick: () => Unit): KeyAddingStage = {
    component(Props(path = path, text = text, onClick = onClick))
  }

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "navLinkButton" -> CSSProperties()
          .setMargin("0 8px")
          .setPadding("2px 4px")
          .setColor("inherit")
          .setTextAlign(TextAlignProperty.inherit)
          .setTextDecoration("none"),
        "navLinkButtonActive" -> CSSProperties()
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    val text = mui.Typography()(props.text).variant(muiStrings.h6).color(Color.inherit)

    router
      .NavLink[String](to = props.path)(text)
      .className(classes("navLinkButton"))
      .activeClassName(s"${classes("navLinkButton")} ${classes("navLinkButtonActive")}")
      .exact(true)
      .onClick(_ => props.onClick())
  }
}
