package net.wiringbits.webapp.utils.ui.web

import net.wiringbits.webapp.utils.api.AdminDataExplorerApiClient

import scala.concurrent.ExecutionContext

case class API(client: AdminDataExplorerApiClient)

object API {

  def apply()(implicit ec: ExecutionContext): API = {
    implicit val sttpBackend = sttp.client.FetchBackend()
    val client = new AdminDataExplorerApiClient.DefaultImpl(AdminDataExplorerApiClient.Config(""))
    API(client)
  }
}
