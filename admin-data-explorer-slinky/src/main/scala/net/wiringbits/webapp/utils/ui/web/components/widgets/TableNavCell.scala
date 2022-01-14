package net.wiringbits.webapp.utils.ui.web.components.widgets

import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiCore.{
  colorsMod => Colors,
  components => mui,
  materialUiCoreStrings => muiStrings
}
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

@react object TableNavCell {
  case class Props(value: String, tableName: String)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "tableCell" -> CSSProperties()
          .setFontSize("0.75rem")
          .setColor("black")
          .setOverflow("hidden")
          .setTextOverflow("ellipsis")
          .setPadding("5px 10px")
          .setBorder("1px solid rgba(0, 0, 0, 0.4)")
          .setBackgroundColor("rgba(0, 0, 0, 0.01)"),
        "viewLink" -> CSSProperties()
          .setFontWeight(600)
          .set("&:hover", CSSProperties().setColor(Colors.teal.`600`))
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    def createHref(tableName: String, value: String): String = {
      s"tables/$tableName/view/$value"
    }

    val link =
      mui
        .Link(snakeCaseToUpper(props.value))
        .className(classes("viewLink"))
        .href(createHref(props.tableName, props.value))
        .underline(muiStrings.none)

    mui
      .TableCell(
        link
      )
      .className(classes("tableCell"))
  }
}
