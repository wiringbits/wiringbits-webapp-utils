package net.wiringbits.webapp.utils.api

import play.api.libs.json._

import java.time.Instant

package object models {

  /**
   * For some reason, play-json doesn't provide support for Instant in the scalajs version,
   * grabbing the jvm values seems to work:
   * - https://github.com/playframework/play-json/blob/master/play-json/jvm/src/main/scala/play/api/libs/json/EnvReads.scala
   * - https://github.com/playframework/play-json/blob/master/play-json/jvm/src/main/scala/play/api/libs/json/EnvWrites.scala
   */
  implicit val instantFormat: Format[Instant] = Format[Instant](
    fjs = implicitly[Reads[String]].map(string => Instant.parse(string)),
    tjs = Writes[Instant](i => JsString(i.toString))
  )

  case class ErrorResponse(error: String)
  implicit val errorResponseFormat: Format[ErrorResponse] = Json.format[ErrorResponse]

  case class AdminGetTablesResponse(data: List[AdminGetTablesResponse.DatabaseTable])
  implicit val adminGetTablesResponseFormat: Format[AdminGetTablesResponse] = Json.format[AdminGetTablesResponse]

  object AdminGetTablesResponse {
    case class DatabaseTable(name: String)
    implicit val adminGetTablesResponseFormat: Format[DatabaseTable] = Json.format[DatabaseTable]
  }

  case class AdminGetTableMetadataResponse(
      name: String,
      fields: List[AdminGetTableMetadataResponse.TableField],
      rows: List[AdminGetTableMetadataResponse.TableRow],
      offSet: Int,
      limit: Int,
      count: Int
  )

  object AdminGetTableMetadataResponse {
    case class TableField(name: String, `type`: String)
    case class TableRow(data: List[Cell])
    case class Cell(value: String)

    implicit val adminGetCellMetadataFormat: Format[AdminGetTableMetadataResponse.Cell] =
      Json.format[AdminGetTableMetadataResponse.Cell]

    implicit val adminGetColumnMetadataFormat: Format[AdminGetTableMetadataResponse.TableField] =
      Json.format[AdminGetTableMetadataResponse.TableField]

    implicit val adminGetRowMetadataFormat: Format[AdminGetTableMetadataResponse.TableRow] =
      Json.format[AdminGetTableMetadataResponse.TableRow]

    implicit val adminGetTableMetadataResponseFormat: Format[AdminGetTableMetadataResponse] =
      Json.format[AdminGetTableMetadataResponse]
  }

  case class AdminFindTableResponse(row: AdminGetTableMetadataResponse.TableRow)

  implicit val adminFindTableResponseFormat: Format[AdminFindTableResponse] =
    Json.format[AdminFindTableResponse]

  case class AdminCreateTableRequest(data: Map[String, String])
  case class AdminCreateTableResponse(noData: String = "")

  implicit val adminCreateTableRequestFormat: Format[AdminCreateTableRequest] =
    Json.format[AdminCreateTableRequest]

  implicit val adminCreateTableResponseFormat: Format[AdminCreateTableResponse] =
    Json.format[AdminCreateTableResponse]

  case class AdminUpdateTableRequest(data: Map[String, String])
  case class AdminUpdateTableResponse(noData: String = "")

  implicit val adminUpdateTableRequestFormat: Format[AdminUpdateTableRequest] =
    Json.format[AdminUpdateTableRequest]

  implicit val adminUpdateTableResponseFormat: Format[AdminUpdateTableResponse] =
    Json.format[AdminUpdateTableResponse]

  case class AdminDeleteTableResponse(noData: String = "")

  implicit val adminDeleteTableResponseFormat: Format[AdminDeleteTableResponse] =
    Json.format[AdminDeleteTableResponse]
}
