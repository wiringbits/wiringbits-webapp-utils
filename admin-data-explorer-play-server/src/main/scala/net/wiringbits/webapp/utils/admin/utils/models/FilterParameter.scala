package net.wiringbits.webapp.utils.admin.utils.models

import net.wiringbits.webapp.utils.admin.utils.StringToDataTypesExt

case class FilterParameter(field: String, value: String)

object FilterParameter {
  def fromString(str: String): FilterParameter = {
    val filter = str.toStringMap
    val field = filter.keys.headOption.getOrElse("")
    val value = filter.values.headOption.getOrElse("")
    FilterParameter(field, value)
  }
}
