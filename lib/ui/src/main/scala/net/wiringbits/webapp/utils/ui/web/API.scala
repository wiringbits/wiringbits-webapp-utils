package net.wiringbits.webapp.utils.ui.web

import net.wiringbits.webapp.utils.api.ApiClient

import scala.concurrent.ExecutionContext

case class API(client: ApiClient)

object API {

  def apply()(implicit ec: ExecutionContext): API = {
    implicit val sttpBackend = sttp.client.FetchBackend()
    val client = new ApiClient.DefaultImpl(ApiClient.Config(""))
    API(client)
  }
}
