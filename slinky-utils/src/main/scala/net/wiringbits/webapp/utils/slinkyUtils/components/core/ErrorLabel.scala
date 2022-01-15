package net.wiringbits.webapp.utils.slinkyUtils.components.core

import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import org.scalablytyped.runtime.StringDictionary
import slinky.core.facade.Fragment
import slinky.core.{FunctionalComponent, KeyAddingStage}

object ErrorLabel {

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "errorLabel" -> CSSProperties()
          .setColor("#f44336")
      )

    makeStyles(stylesCallback, WithStylesOptions())
  }
  case class Props(text: String)

  def apply(text: String): KeyAddingStage = {
    component(Props(text))
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    if (props.text.nonEmpty) {
      Fragment(
        mui
          .Typography(props.text)
          .className(classes("errorLabel"))
          .variant(muiStrings.body2)
      )
    } else {
      Fragment()
    }
  }
}
