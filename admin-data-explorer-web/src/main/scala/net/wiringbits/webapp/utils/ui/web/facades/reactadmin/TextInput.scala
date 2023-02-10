package net.wiringbits.webapp.utils.ui.web.facades.reactadmin

import slinky.core.ExternalComponent

import scala.scalajs.js
import scala.scalajs.js.|

object TextInput extends ExternalComponent {
  case class Props(source: String, disabled: Boolean = false)
  override val component: String | js.Object = ReactAdmin.TextInput
}
