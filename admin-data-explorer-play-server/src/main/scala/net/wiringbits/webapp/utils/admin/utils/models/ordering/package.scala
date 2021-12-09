package net.wiringbits.webapp.utils.admin.utils.models

import net.wiringbits.webapp.common.models.WrappedString

package object ordering {
  case class OrderingCondition(string: String) extends WrappedString
}
