package net.wiringbits.webapp.utils.admin

import net.wiringbits.webapp.utils.admin.utils.models.QueryParameters
import play.api.libs.json.Json

package object utils {
  implicit class StringToDataTypesExt(val str: String) extends AnyVal {
    // convert ["id", "ASC"] string to List("id", "ASC")
    implicit def toStringList: List[String] = {
      str.substring(1, str.length - 1).filterNot(x => x == '"').split(",").toList
    }

    // convert json object string (for example: "{}") to Map
    implicit def toStringMap: Map[String, String] = {
      val maybe = Json.parse(str).validate[Map[String, String]]
      maybe.getOrElse(Map.empty)
    }
  }

  implicit class MapStringHideExt(val data: Map[String, String]) extends AnyVal {
    def hideData(columnsToHide: List[String]): Map[String, String] = {
      data.filterNot { case (key, _) => columnsToHide.contains(key) }
    }
  }

  def contentRangeHeader(tableName: String, queryParameters: QueryParameters, numberOfRecords: Int): String = {
    s"$tableName ${queryParameters.pagination.start}-${queryParameters.pagination.end}/$numberOfRecords"
  }
}
