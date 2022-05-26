package net.wiringbits.webapp.utils.admin.utils.models

import net.wiringbits.webapp.utils.admin.utils.StringToDataTypesExt

import scala.util.Try

case class PaginationParameter(start: Int, end: Int)

object PaginationParameter {
  // transforms a string like: "[0,9]" into this model
  def fromString(str: String): PaginationParameter = {
    val range = str.toStringList.map(Integer.parseInt)
    val start = range.headOption.getOrElse(0)
    val end = Try(range(1)).getOrElse(9)
    PaginationParameter(start = start, end = end)
  }
}
