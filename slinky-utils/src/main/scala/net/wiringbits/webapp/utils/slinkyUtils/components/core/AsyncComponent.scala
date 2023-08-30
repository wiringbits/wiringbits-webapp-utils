package net.wiringbits.webapp.utils.slinkyUtils.components.core

import com.olvind.mui.muiMaterial.components as mui
import com.olvind.mui.muiMaterial.mod.PropTypes.Color
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.*
import slinky.core.facade.{Hooks, ReactElement}
import slinky.core.{FunctionalComponent, KeyAddingStage}

import scala.concurrent.Future
import scala.util.{Failure, Success}

/** A reusable component to renders data from a asynchronous data source, providing:
  *   - A progress indicator when the data is being retrieved.
  *   - Invoking the render function when the data is available, to render such data.
  *   - Displaying an error message when retrieving the data has failed, as well as displaying a retry button so that
  *     the user is able to try again.
  */
object AsyncComponent {

  sealed trait DataState[T] extends Product with Serializable {
    def loaded(data: T): DataState.Loaded[T] = DataState.Loaded(data)
    def failed(msg: String): DataState.Failed[T] = DataState.Failed(msg)
  }

  object DataState {
    case class Loading[T]() extends DataState[T]
    case class Loaded[T](data: T) extends DataState[T]
    case class Failed[T](msg: String) extends DataState[T]

    def loading[T]: DataState[T] = Loading[T]()
  }

  /** @param fetch
    *   the function to fetch the data
    * @param render
    *   the function to render the data once it is available
    * @param onDataLoaded
    *   a function invoked when the remote data has been loaded
    * @param progressIndicator
    *   the component rendered when the data is being loaded
    * @param progressIndicatorWhileReloadingData
    *   whether to display the progress indicator every time the data is being reloaded
    * @param retryLabel
    *   the label to use in the button that retries the operation
    * @param watchedObjects
    *   objects being watched, when any of those changes, the data is loaded again
    */
  case class Props[D](
      fetch: () => Future[D],
      render: D => ReactElement,
      onDataLoaded: D => Unit = (_: D) => (),
      progressIndicator: () => ReactElement = () => loader,
      progressIndicatorWhileReloadingData: Boolean = false,
      retryLabel: String = "Retry",
      watchedObjects: Iterable[Any] = List("")
  )

  def apply[D](
      fetch: () => Future[D],
      render: D => ReactElement,
      onDataLoaded: D => Unit = (_: D) => (),
      progressIndicator: () => ReactElement = () => loader,
      progressIndicatorWhileReloadingData: Boolean = false,
      retryLabel: String = "Retry",
      watchedObjects: Iterable[Any] = List("")
  ): KeyAddingStage = {
    component[D](
      Props(
        fetch,
        render,
        onDataLoaded,
        progressIndicator,
        progressIndicatorWhileReloadingData,
        retryLabel,
        watchedObjects
      )
    )
  }

  /** @tparam D
    *   The data to fetch and render
    * @return
    *   the component
    */
  def component[D]: FunctionalComponent[Props[D]] = FunctionalComponent[Props[D]] { props =>
    val (dataState, setDataState) = Hooks.useState[DataState[D]](DataState.loading[D])

    def reload(): Unit = {
      def f(state: DataState[D]): DataState[D] = state match {
        case _: DataState.Failed[D] => DataState.loading[D]
        case _ =>
          if (props.progressIndicatorWhileReloadingData) DataState.loading[D]
          else state
      }
      setDataState(f _)
      props.fetch().onComplete {
        case Success(value) =>
          setDataState(_.loaded(value))
          props.onDataLoaded(value)

        case Failure(ex) => setDataState(_.failed(ex.getMessage))
      }
    }

    Hooks.useEffect(reload _, props.watchedObjects)

    dataState match {
      case DataState.Loading() =>
        props.progressIndicator()

      case DataState.Loaded(data) =>
        props.render(data)

      case DataState.Failed(msg) =>
        error(msg, props, () => reload())
    }
  }

  private def loader: ReactElement = {
    mui.Box(
      mui.CircularProgress()
    )
  }

  private def error[D](msg: String, props: Props[D], reload: () => Unit): ReactElement = {
    mui.Box(
      mui
        .Typography()
        .color(Color.secondary)(msg),
      mui.Button.normal.onClick(_ => reload())(props.retryLabel)
    )
  }
}
