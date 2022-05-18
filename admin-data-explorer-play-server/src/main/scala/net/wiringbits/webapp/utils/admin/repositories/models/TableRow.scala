package net.wiringbits.webapp.utils.admin.repositories.models

import scala.collection.immutable.ListMap

case class TableRow(
    data: List[Cell]
) {
  def convertToMap(fieldNames: List[String]): Map[String, String] = {
    val rowData = data.map(_.value)
    val test = fieldNames.zip(rowData)
    ListMap.from(test)
  }
}
