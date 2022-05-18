package net.wiringbits.webapp.utils.admin.utils.models

case class QueryParameters(sort: SortParameter, pagination: PaginationParameter, filter: FilterParameter) {
  override def toString: String = {
    s"sort = ${sort.field} ${sort.ordering}, pagination = ${pagination.start} to ${pagination.end}, filter = ${filter.field}=${filter.value}"
  }
}
