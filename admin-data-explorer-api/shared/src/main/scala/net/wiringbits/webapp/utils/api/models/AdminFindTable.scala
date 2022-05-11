package net.wiringbits.webapp.utils.api.models

import play.api.libs.json.{Format, Json}

object AdminFindTable {
  case class Response(
      data: Map[String, String]
  )

  implicit val adminFindTableResponseFormat: Format[Response] =
    Json.format[Response]
}
