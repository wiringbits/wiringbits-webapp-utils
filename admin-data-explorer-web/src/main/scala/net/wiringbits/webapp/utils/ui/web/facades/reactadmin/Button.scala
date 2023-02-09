package net.wiringbits.webapp.utils.ui.web.facades.reactadmin

import slinky.core.ExternalComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement

import scala.scalajs.js
import scala.scalajs.js.|

@react
object Button extends ExternalComponent {
  case class Props(onClick: () => Unit, children: ReactElement*)
  override val component: String | js.Object = ReactAdmin.Button
}
