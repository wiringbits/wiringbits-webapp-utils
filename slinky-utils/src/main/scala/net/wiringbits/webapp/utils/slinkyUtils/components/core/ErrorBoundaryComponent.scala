package net.wiringbits.webapp.utils.slinkyUtils.components.core

import slinky.core.ComponentWrapper
import slinky.core.facade.{ErrorBoundaryInfo, ReactElement}

import scala.scalajs.js

object ErrorBoundaryComponent extends ComponentWrapper {
  case class Props(child: ReactElement, renderError: js.Error => ReactElement)
  case class State(error: Option[js.Error])

  class Def(jsProps: js.Object) extends Definition(jsProps) {
    override def initialState: State = State(None)

    override def componentDidCatch(error: js.Error, info: ErrorBoundaryInfo): Unit = {
      setState(_ => State(Option(error)))
      org.scalajs.dom.console.error("Unexpected error found", error, info.componentStack)
    }

    override def render(): ReactElement = {
      state.error match {
        case Some(error) => props.renderError(error)
        case None => props.child
      }
    }
  }
}
