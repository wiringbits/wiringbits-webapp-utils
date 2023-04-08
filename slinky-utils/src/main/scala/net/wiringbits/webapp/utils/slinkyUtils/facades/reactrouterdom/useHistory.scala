package net.wiringbits.webapp.utils.slinkyUtils.facades.reactrouterdom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport("react-router-dom", "useHistory")
@js.native
object useHistory extends js.Object {
  def apply(): useHistory.type = js.native
  val length: Double = js.native
  def push(path: String): Unit = js.native
  def replace(path: String): Unit = js.native
  def go(n: Int): Unit = js.native
  def goBack(): Unit = js.native
  def goForward(): Unit = js.native
}
