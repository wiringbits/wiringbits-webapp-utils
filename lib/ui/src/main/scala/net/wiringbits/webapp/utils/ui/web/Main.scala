package net.wiringbits.webapp.utils.ui.web

import net.wiringbits.webapp.utils.ui.components.core.{ErrorBoundaryComponent, ErrorBoundaryInfo}
import org.scalajs.dom
import slinky.web.ReactDOM

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js

@JSImport("js/index.css", JSImport.Default)
@js.native
object IndexCSS extends js.Object

object Main {
  val css = IndexCSS

  def main(argv: Array[String]): Unit = {
    val app = ErrorBoundaryComponent(
      ErrorBoundaryComponent.Props(
        child = App(API()),
        renderError = e => ErrorBoundaryInfo(e)
      )
    )

    ReactDOM.render(app, dom.document.getElementById("root"))
  }
}
