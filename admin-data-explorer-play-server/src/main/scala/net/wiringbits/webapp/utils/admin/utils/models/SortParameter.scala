package net.wiringbits.webapp.utils.admin.utils.models

import net.wiringbits.webapp.utils.admin.utils.StringToDataTypesExt

case class SortParameter(field: String, ordering: String) {
  def fromPrimaryKeyField(primaryKeyField: String): SortParameter = {
    val sortField = Option
      .when(field == "id")(primaryKeyField)
      .getOrElse(field)
    SortParameter(sortField, ordering)
  }
}

object SortParameter {
  def fromString(str: String): SortParameter = {
    val sort = str.toStringList
    SortParameter(field = sort.head, ordering = sort(1))
  }
}
