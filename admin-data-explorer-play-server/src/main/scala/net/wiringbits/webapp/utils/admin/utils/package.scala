package net.wiringbits.webapp.utils.admin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import net.wiringbits.webapp.utils.admin.utils.models.QueryParameters

package object utils {
  implicit class StringToDataTypesExt(val str: String) extends AnyVal {
    // convert ["id", "ASC"] string to List("id", "ASC")
    implicit def toStringList: List[String] = {
      str.substring(1, str.length - 1).filterNot(x => x == '"').split(",").toList
    }

    // convert json object string (for example: "{}") to Map
    implicit def toStringMap: Map[String, String] = {
      val mapper = new ObjectMapper()
      mapper.registerModule(DefaultScalaModule)
      mapper.readValue(str, classOf[Map[String, String]])
    }
  }

  def contentRangeHeader(tableName: String, queryParameters: QueryParameters, numberOfRecords: Int): String = {
    s"$tableName ${queryParameters.pagination.start}-${queryParameters.pagination.end}/$numberOfRecords"
  }
}
