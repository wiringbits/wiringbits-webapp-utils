package net.wiringbits.webapp.utils.slinkyUtils.facades.reactrouterdom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport("react-router-dom", "useLocation")
@js.native
object useLocation extends js.Object {
  def apply(): useLocation.type = js.native
  val key: String = js.native
  val pathname: String = js.native
  val search: String = js.native
  val hash: String = js.native
}
