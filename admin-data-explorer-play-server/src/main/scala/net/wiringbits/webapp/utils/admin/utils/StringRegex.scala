package net.wiringbits.webapp.utils.admin.utils

import scala.util.matching.Regex

object StringRegex {
  val dateRegex: Regex = "([12]\\d{3})-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])".r
}
