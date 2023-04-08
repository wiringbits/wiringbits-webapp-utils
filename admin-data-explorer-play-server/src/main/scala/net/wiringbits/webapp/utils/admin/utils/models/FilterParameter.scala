package net.wiringbits.webapp.utils.admin.utils.models

import net.wiringbits.webapp.utils.admin.utils.StringToDataTypesExt

case class FilterParameter(field: String, value: String) {
  override def toString: String = s"$field=$value"
}

object FilterParameter {
  def fromString(str: String): List[FilterParameter] = {
    val filters = str.toStringMap
    filters.map { case (field, value) =>
      FilterParameter(field, value)
    }.toList
  }
}
