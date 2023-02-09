package net.wiringbits.webapp.utils.ui.web.facades.reactadmin

import slinky.core.ExternalComponent
import slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.|

@react
object ImageField extends ExternalComponent {
  case class Props(source: String, disabled: Boolean = false, sx: js.Dynamic = js.Dynamic.literal())
  override val component: String | js.Object = ReactAdmin.ImageField
}
