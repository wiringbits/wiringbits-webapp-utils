package net.wiringbits.webapp.utils.api.models

import play.api.libs.json.{Format, Json}

object AdminGetTableMetadata {
  case class Response(
      name: String,
      fields: List[Response.TableField],
      rows: List[Response.TableRow],
      offSet: Int,
      limit: Int,
      count: Int
  )
  object Response {
    case class TableField(name: String, `type`: String)
    case class TableRow(data: List[Cell])
    case class Cell(value: String)

    implicit val adminGetCellMetadataFormat: Format[Cell] =
      Json.format[Cell]

    implicit val adminGetColumnMetadataFormat: Format[TableField] =
      Json.format[TableField]

    implicit val adminGetRowMetadataFormat: Format[TableRow] =
      Json.format[TableRow]

  }

  implicit val adminGetTableMetadataResponseFormat: Format[Response] =
    Json.format[Response]
}
