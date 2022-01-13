package net.wiringbits.webapp.utils.ui.web.components.widgets

import com.alexitc.materialui.facade.csstype.mod.TableLayoutProperty
import com.alexitc.materialui.facade.materialUiCore.createMuiThemeMod.Theme
import com.alexitc.materialui.facade.materialUiStyles.makeStylesMod.StylesHook
import com.alexitc.materialui.facade.materialUiStyles.mod.makeStyles
import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import com.alexitc.materialui.facade.materialUiStyles.withStylesMod.{
  CSSProperties,
  StyleRulesCallback,
  Styles,
  WithStylesOptions
}
import net.wiringbits.webapp.utils.api.models.AdminGetTableMetadataResponse
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{Container, Subtitle}
import net.wiringbits.webapp.utils.ui.web.utils.snakeCaseToUpper
import org.scalablytyped.runtime.StringDictionary
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment

@react object ExperimentalTable {
  case class Props(response: AdminGetTableMetadataResponse)

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = _ =>
      StringDictionary(
        "table" -> CSSProperties()
          .setTableLayout(TableLayoutProperty.auto)
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    val title = Subtitle(snakeCaseToUpper(props.response.name))

    val columns = props.response.fields.map(field => TableField(field.name))
    val rows = props.response.rows.map(row => TableRow(row, props.response.name))
    val table =
      mui
        .Table(
          mui.TableHead(
            columns
          ),
          mui.TableBody(
            rows
          )
        )
        .className(classes("table"))

    Container(
      maxWidth = Some("100%"),
      child = Fragment(
        title,
        table,
        Pagination(props.response)
      )
    )

  }

}
