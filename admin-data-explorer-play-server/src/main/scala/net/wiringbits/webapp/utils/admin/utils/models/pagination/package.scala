package net.wiringbits.webapp.utils.admin.utils.models

import net.wiringbits.webapp.common.models.WrappedInt

package object pagination {
  case class Count(int: Int) extends WrappedInt

  case class Offset(int: Int) extends WrappedInt

  case class Limit(int: Int) extends WrappedInt

  case class PaginatedQuery(offset: Offset, limit: Limit)

  case class PaginatedResult[+T](data: T, offset: Offset, limit: Limit, total: Count)
}
