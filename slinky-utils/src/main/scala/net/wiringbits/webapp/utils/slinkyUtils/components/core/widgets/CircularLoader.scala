package net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets

import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import org.scalablytyped.runtime.StringDictionary
import slinky.core.{FunctionalComponent, KeyAddingStage}

object CircularLoader {
  case class Props(size: Int = 16)

  def apply(size: Int = 16): KeyAddingStage = {
    component(Props(size = size))
  }

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "circularLoader" -> CSSProperties()
          .setDisplay("flex")
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())
    mui
      .CircularProgress()
      .className(classes("circularLoader"))
      .size(props.size)
  }
}
