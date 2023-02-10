package net.wiringbits.webapp.utils.ui.web.facades.reactadmin

import slinky.core.ExternalComponent

import scala.scalajs.js
import scala.scalajs.js.|

object Resource extends ExternalComponent {
  case class Props(name: String, edit: js.Object, list: js.Object)
  override val component: String | js.Object = ReactAdmin.Resource
}
