package net.wiringbits.webapp.utils.ui.web.components.widgets

import com.alexitc.materialui.facade.csstype.mod.TableLayoutProperty
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
import net.wiringbits.webapp.utils.api.models.AdminGetTableMetadataResponse
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{Container, Subtitle}
import net.wiringbits.webapp.utils.ui.web.utils.snakeCaseToUpper
import org.scalablytyped.runtime.StringDictionary
import slinky.core.facade.{Fragment, ReactElement}
import slinky.core.{FunctionalComponent, KeyAddingStage}

object ExperimentalTable {
  case class Props(response: AdminGetTableMetadataResponse)

  def apply(response: AdminGetTableMetadataResponse): KeyAddingStage = {
    component(Props(response = response))
  }

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = theme =>
      StringDictionary(
        "table" -> CSSProperties()
          .setTableLayout(TableLayoutProperty.fixed)
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val classes = useStyles(())

    val columns = props.response.fields.map { field =>
      Cell(field.name, props.response.name, isField = true)
    }

    val rows: List[ReactElement] = props.response.rows.map { row =>
      mui
        .TableRow(
          row.data.map { cell =>
            // Here I'm supossing that the first column belongs to table ID
            if (row.data.indexOf(cell) == 0) Cell(cell.value, props.response.name, isNav = true)
            else Cell(cell.value, props.response.name)
          }
        )
    }

    val table: ReactElement = mui
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
        Subtitle(snakeCaseToUpper(props.response.name)),
        table,
        Pagination(props.response)
      )
    )
  }
}
