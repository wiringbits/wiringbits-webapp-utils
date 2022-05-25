package net.wiringbits.webapp.utils.admin.utils.models

import net.wiringbits.webapp.utils.admin.utils.StringToDataTypesExt

import scala.util.Try

case class SortParameter(field: String, ordering: String)

object SortParameter {
  def fromString(str: String): SortParameter = {
    val sort = str.toStringList
    val field = sort.headOption.getOrElse("")
    val ordering = Try(sort(1)).getOrElse("")
    SortParameter(field = field, ordering = ordering)
  }
}
