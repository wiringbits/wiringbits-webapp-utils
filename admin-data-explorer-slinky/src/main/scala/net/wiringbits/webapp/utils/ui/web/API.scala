package net.wiringbits.webapp.utils.ui.web

import net.wiringbits.webapp.utils.api.AdminDataExplorerApiClient

import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits._

case class API(client: AdminDataExplorerApiClient)

object API {

  def apply(): API = {
    implicit val sttpBackend = sttp.client.FetchBackend()
    val client = new AdminDataExplorerApiClient.DefaultImpl(AdminDataExplorerApiClient.Config(""))
    API(client)
  }
}
