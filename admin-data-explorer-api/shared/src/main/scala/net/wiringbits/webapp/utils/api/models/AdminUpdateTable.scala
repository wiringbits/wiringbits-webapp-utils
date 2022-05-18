package net.wiringbits.webapp.utils.api.models

import play.api.libs.json.{Format, Json}

object AdminUpdateTable {
  case class Request(data: Map[String, String])
  case class Response(id: String)

  implicit val adminUpdateTableRequestFormat: Format[Request] =
    Json.format[Request]

  implicit val adminUpdateTableResponseFormat: Format[Response] =
    Json.format[Response]
}
