package net.wiringbits.webapp.utils.admin.utils.models

import net.wiringbits.webapp.utils.admin.utils.StringToDataTypesExt

case class PaginationParameter(start: Int, end: Int)

object PaginationParameter {
  // transforms a string like: "[0,9]" into this model
  def fromString(str: String): PaginationParameter = {
    val range = str.toStringList.map(Integer.parseInt)
    PaginationParameter(start = range.head, end = range(1))
  }
}
