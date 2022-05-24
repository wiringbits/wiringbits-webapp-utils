package net.wiringbits.webapp.utils.api.models

import play.api.libs.json.{Format, Json}

object AdminGetTables {
  case class Response(data: List[Response.DatabaseTable])
  object Response {
    case class DatabaseTable(name: String, fields: List[TableField], primaryKeyName: String)
    case class TableField(name: String, `type`: String, editable: Boolean, reference: Option[String])

    implicit val adminTableFieldResponseFormat: Format[TableField] = Json.format[TableField]
    implicit val adminDatabaseTableResponseFormat: Format[DatabaseTable] = Json.format[DatabaseTable]
  }
  implicit val adminGetTablesResponseFormat: Format[Response] = Json.format[Response]
}
