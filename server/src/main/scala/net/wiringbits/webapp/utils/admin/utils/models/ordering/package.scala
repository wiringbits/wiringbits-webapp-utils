package net.wiringbits.webapp.utils.admin.utils.models

import net.wiringbits.webapp.utils.common.models.core.WrappedString

package object ordering {
  case class OrderingCondition(string: String) extends AnyVal with WrappedString
}
