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
import net.wiringbits.webapp.utils.ui.web.utils.formatCellValue
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.{Fragment, Hooks}

@react object TableCell {
  case class Props(value: String, tableName: String, isNav: Boolean = false)

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
          .setBackgroundColor("rgba(0, 0, 0, 0.01)")
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())
    val (open, setOpen) = Hooks.useState(false)

    val dialog = mui
      .Popover(open)(props.value)
      .onClose(_ => setOpen(false))

    val tableCell = mui
      .TableCell(
        formatCellValue(props.value)
      )
      .onClick(_ => setOpen(true))
      .className(classes("tableCell"))

    Fragment(
      tableCell,
      dialog
    )
  }

}
