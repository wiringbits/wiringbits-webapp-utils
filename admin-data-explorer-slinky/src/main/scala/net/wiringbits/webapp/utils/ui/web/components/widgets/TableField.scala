package net.wiringbits.webapp.utils.ui.web.components.widgets

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
import net.wiringbits.webapp.utils.ui.web.utils.snakeCaseToUpper
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react

@react object TableField {
  case class Props(value: String)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = _ =>
      StringDictionary(
        "tableField" -> CSSProperties()
          .setFontSize("0.75rem")
          .setColor("black")
          .setOverflow("hidden")
          .setTextOverflow("ellipsis")
          .setPadding("10px")
          .setBorder("1px solid rgba(0, 0, 0, 0.4)")
          .setBackgroundColor("rgba(0, 0, 0, 0.02)")
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    mui
      .TableCell(
        snakeCaseToUpper(props.value)
      )
      .className(classes("tableField"))
  }
}
