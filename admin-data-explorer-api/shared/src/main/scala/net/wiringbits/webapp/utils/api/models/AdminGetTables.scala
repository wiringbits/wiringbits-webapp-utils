package net.wiringbits.webapp.utils.api.models

import play.api.libs.json.{Format, Json}

object AdminGetTables {
  case class Response(data: List[Response.DatabaseTable])
  object Response {
    case class DatabaseTable(name: String)
    implicit val adminGetTablesResponseFormat: Format[DatabaseTable] = Json.format[DatabaseTable]
  }
  implicit val adminGetTablesResponseFormat: Format[Response] = Json.format[Response]
}
