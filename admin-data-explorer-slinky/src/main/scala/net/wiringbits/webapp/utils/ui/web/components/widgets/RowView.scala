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
import net.wiringbits.facades.reactRouter.mod.useHistory
import net.wiringbits.webapp.utils.api.models.AdminFindTable
import net.wiringbits.webapp.utils.slinkyUtils.components.core.ErrorLabel
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{Container, Subtitle}
import net.wiringbits.webapp.utils.ui.web.API
import net.wiringbits.webapp.utils.ui.web.utils.{formatCellValue, snakeCaseToUpper}
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.global
import slinky.core.facade.{Fragment, Hooks}
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.web.html.{className, div}

import scala.util.{Failure, Success}

object RowView {
  case class Props(api: API, response: AdminFindTable.Response, tableName: String, ID: String)

  def apply(api: API, response: AdminFindTable.Response, tableName: String, ID: String): KeyAddingStage = {
    component(Props(api = api, response = response, tableName = tableName, ID = ID))
  }

  private lazy val useStyles: StylesHook[Styles[Theme, Unit, String]] = {
    val stylesCallback: StyleRulesCallback[Theme, Unit, String] = _ =>
      StringDictionary(
        "actions" -> CSSProperties()
          .setAlignItems("center"),
        "tableItem" -> CSSProperties()
      )
    makeStyles(stylesCallback, WithStylesOptions())
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val history = useHistory()
    val classes = useStyles(())
    val (error, setError) = Hooks.useState[Option[String]](Option.empty)

    val header = Container(
      margin = Container.EdgeInsets.bottom(16),
      child = Subtitle(snakeCaseToUpper(props.tableName))
    )

    val body =
      props.response.row.data.map { cell =>
        val currentIndex = props.response.row.data.indexOf(cell)
        val fieldName = props.response.fields(currentIndex).name
        CellInput(
          label = fieldName,
          initialValue = formatCellValue(cell.value),
          onChange = _ => (),
          disabled = true
        )
      }

    def createUrl(): String = {
      s"/tables/${props.tableName}/update/${props.ID}"
    }

    def deleteRecord(): Unit = {
      props.api.client.deleteItem(props.tableName, props.ID).onComplete {
        case Success(_) =>
          history.goBack()
          setError(None)
        case Failure(ex) =>
          setError(Some(ex.toString))
      }
    }

    val actions = div(className := classes("actions"))(
      Fragment(
        UpdateButton(() => history.push(createUrl())),
        DeleteButton(() => deleteRecord()),
        mui
          .Button()("Back")
          .onClick(_ => history.goBack())
      )
    )

    Fragment(
      header,
      body,
      actions,
      error.map { ex =>
        ErrorLabel(ex)
      }
    )
  }
}
