package net.wiringbits.webapp.utils.ui.web

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

package object facades {
  @js.native
  @JSImport("ra-data-simple-rest", JSImport.Default)
  def simpleRestProvider(url: String): DataProvider = js.native
}
