package net.wiringbits.webapp.utils.api.models

import play.api.libs.json.{Format, Json}

object AdminCreateTable {
  case class Request(data: Map[String, String])

  case class Response(noData: String = "")

  implicit val adminCreateTableRequestFormat: Format[Request] =
    Json.format[Request]

  implicit val adminCreateTableResponseFormat: Format[Response] =
    Json.format[Response]

}
