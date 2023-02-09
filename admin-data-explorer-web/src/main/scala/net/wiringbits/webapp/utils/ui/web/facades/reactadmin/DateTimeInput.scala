package net.wiringbits.webapp.utils.ui.web.facades.reactadmin

import slinky.core.ExternalComponent
import slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.|

@react
object DateTimeInput extends ExternalComponent {
  case class Props(source: String, disabled: Boolean = false)
  override val component: String | js.Object = ReactAdmin.DateTimeInput
}
