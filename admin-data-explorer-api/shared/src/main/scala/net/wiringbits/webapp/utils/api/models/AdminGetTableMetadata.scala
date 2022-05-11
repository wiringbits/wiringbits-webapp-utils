package net.wiringbits.webapp.utils.api.models

import play.api.libs.json.{Format, Json}

object AdminGetTableMetadata {
  case class Response(data: List[Map[String, String]])

  implicit val adminGetTableMetadataResponseFormat: Format[Response] =
    Json.format[Response]
}
