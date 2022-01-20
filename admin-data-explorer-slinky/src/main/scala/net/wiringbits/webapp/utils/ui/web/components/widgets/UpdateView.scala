package net.wiringbits.webapp.utils.ui.web.components.widgets

import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import net.wiringbits.facades.reactRouter.mod.useHistory
import net.wiringbits.webapp.utils.api.models.{AdminFindTable, AdminUpdateTable}
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{Container, Subtitle}
import net.wiringbits.webapp.utils.ui.web.API
import net.wiringbits.webapp.utils.ui.web.utils.{getChangedValues, snakeCaseToUpper}
import org.scalajs.dom.console
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.global
import slinky.core.facade.{Fragment, Hooks}
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.web.html.div

import scala.util.{Failure, Success}

object UpdateView {
  case class Props(api: API, tableName: String, ID: String)

  def apply(api: API, tableName: String, ID: String): KeyAddingStage = {
    component(Props(api = api, tableName = tableName, ID = ID))
  }

  private case class State(
      loading: Boolean,
      initialValue: Option[AdminFindTable.Response],
      value: Option[AdminFindTable.Response],
      hasChanges: Boolean,
      error: Option[String]
  )

  private val initialState = State(
    loading = false,
    Option.empty,
    Option.empty,
    hasChanges = false,
    Option.empty
  )
  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val (state, setState) = Hooks.useState(initialState)
    val history = useHistory()

    def fetchItem(): Unit = {
      setState(_.copy(loading = true))
      props.api.client.viewItem(props.tableName, props.ID).onComplete {
        case Success(res) =>
          setState(_.copy(loading = false, value = Some(res), initialValue = Some(res)))
        case Failure(ex) =>
          setState(_.copy(loading = false, error = Some(ex.toString)))
      }
    }

    def onItemValueChange(updatedItem: AdminFindTable.Response): Unit = {
      val initialValue = state.initialValue.get
      val hasChanges = !(initialValue.row.data sameElements updatedItem.row.data)

      setState(
        _.copy(
          value = Some(updatedItem),
          hasChanges = hasChanges
        )
      )
    }

    def onSaveClick(): Unit = {
      setState(_.copy(loading = true))
      state.value.foreach { res =>
        state.initialValue.foreach { initialValue =>
          val fieldsName = res.fields.map(_.name)
          val rowValues = res.row.data.map(_.value)
          val initialRowValues = initialValue.row.data.map(_.value)

          val changedValues =
            getChangedValues(fieldNames = fieldsName, initialValues = initialRowValues, values = rowValues)
          val request = AdminUpdateTable.Request(data = changedValues)

          // TODO: Remove this console logs
          console.log(changedValues.toString)
          console.log(request.toString)
          props.api.client.updateItem(props.tableName, props.ID, request).onComplete {
            case Success(_) =>
              setState(_.copy(loading = false, value = state.value, initialValue = state.value, hasChanges = false))
            case Failure(ex) =>
              setState(_.copy(loading = false, error = Some(ex.toString)))
          }
        }
      }
    }

    Hooks.useEffect(() => fetchItem(), "")

    val header = Container(
      margin = Container.EdgeInsets.bottom(16),
      child = Subtitle(snakeCaseToUpper(props.tableName))
    )

    def renderBody(response: AdminFindTable.Response) = {
      val body = EditItemView(response, onItemValueChange)

      val actions = Fragment(
        mui
          .Button()("Back")
          .onClick(_ => history.goBack()),
        mui
          .Button()("Save")
          .variant(muiStrings.contained)
          .color(muiStrings.primary)
          .disabled(!state.hasChanges)
          .onClick(_ => onSaveClick())
      )

      Fragment(
        body,
        actions
      )
    }

    Fragment(
      header,
      if (state.loading) Loader()
      else
        state.value match {
          case Some(user) => renderBody(user)
          case None => div("Element not found")
        }
    )
  }
}
