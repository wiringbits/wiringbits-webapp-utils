package net.wiringbits.webapp.utils.api.models

import play.api.libs.json.{Format, Json}

object AdminGetTables {
  case class Response(data: List[Response.DatabaseTable])
  object Response {
    case class DatabaseTable(name: String, columns: List[TableColumn], primaryKeyName: String, canBeDeleted: Boolean)
    case class TableColumn(
        name: String,
        `type`: String,
        editable: Boolean,
        reference: Option[TableReference],
        filterable: Boolean
    )
    case class TableReference(referencedTable: String, referenceField: String)

    implicit val adminTableReferenceResponseFormat: Format[TableReference] = Json.format[TableReference]
    implicit val adminTableColumnResponseFormat: Format[TableColumn] = Json.format[TableColumn]
    implicit val adminDatabaseTableResponseFormat: Format[DatabaseTable] = Json.format[DatabaseTable]
  }
  implicit val adminGetTablesResponseFormat: Format[Response] = Json.format[Response]
}
