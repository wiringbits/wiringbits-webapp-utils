package net.wiringbits.webapp.utils.ui.web

import net.wiringbits.webapp.utils.api.AdminDataExplorerApiClient
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits._
import sttp.client3.SttpBackend

import scala.concurrent.Future

case class API(client: AdminDataExplorerApiClient)

object API {

  def apply(): API = {
    implicit val sttpBackend: SttpBackend[Future, _] = sttp.client3.FetchBackend()
    val client = new AdminDataExplorerApiClient.DefaultImpl(AdminDataExplorerApiClient.Config(""))
    API(client)
  }
}
