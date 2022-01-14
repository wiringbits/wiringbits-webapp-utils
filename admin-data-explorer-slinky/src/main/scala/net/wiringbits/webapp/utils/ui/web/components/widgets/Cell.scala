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
import com.alexitc.materialui.facade.std.HTMLTableHeaderCellElement
import net.wiringbits.webapp.utils.ui.web.utils.snakeCaseToUpper
import org.scalablytyped.runtime.StringDictionary
import slinky.core.facade.{Fragment, Hooks}
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.web.SyntheticMouseEvent

object Cell {
  case class Props(value: String, tableName: String, isField: Boolean = false, isNav: Boolean = false)

  def apply(value: String, tableName: String, isField: Boolean = false, isNav: Boolean = false): KeyAddingStage = {
    component(Props(value = value, tableName = tableName, isField = isField, isNav = isNav))
  }

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "tableCell" -> CSSProperties()
          .setFontSize("0.75rem")
          .setColor("black")
          .setOverflow("hidden")
          .setTextOverflow("ellipsis")
          .setPadding("10px 15px")
          .setBorder("1px solid rgba(0, 0, 0, 0.4)")
          .setBackgroundColor("rgba(0, 0, 0, 0.01)"),
        "tableField" -> CSSProperties()
          .setFontSize("0.75rem")
          .setColor("black")
          .setOverflow("hidden")
          .setTextOverflow("ellipsis")
          .setPadding("10px 15px")
          .setBorder("1px solid rgba(0, 0, 0, 0.4)")
          .setBackgroundColor("rgba(0, 0, 0, 0.02)"),
        "updateLink" -> CSSProperties()
          .setFontWeight(600)
          .set("&:hover", CSSProperties().setColor(Colors.teal.`600`))
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())
    val (open, setOpen) = Hooks.useState(false)

    def removeNull(value: String): String = {
      if (value.equals("null")) "" else value
    }

    def handleMouseOver(event: SyntheticMouseEvent[HTMLTableHeaderCellElement]): Unit = {
      setOpen(true)
    }

    def handleMouseLeave(): Unit = {
      setOpen(false)
    }

    val dialog = mui
      .Popover(open)(props.value)
      .onClose(_ => handleMouseLeave())

    def createHref(tableName: String, value: String): String = {
      // TODO: Slugify
      val url = s"tables/$tableName/update/$value"
      url
    }

    var tableCell = mui
      .TableCell(
        removeNull(props.value)
      )
      .onClick(event => handleMouseOver(event))
      .className(classes("tableCell"))

    if (props.isNav) {
      tableCell = mui
        .TableCell(
          mui
            .Link(snakeCaseToUpper(props.value))
            .className(classes("updateLink"))
            .href(createHref(props.tableName, props.value))
            .underline(muiStrings.none)
        )
        .className(classes("tableField"))
    }

    if (props.isField) {
      tableCell = mui
        .TableCell(
          snakeCaseToUpper(props.value)
        )
        .className(classes("tableField"))
    }

    Fragment(
      tableCell,
      dialog
    )
  }

}
