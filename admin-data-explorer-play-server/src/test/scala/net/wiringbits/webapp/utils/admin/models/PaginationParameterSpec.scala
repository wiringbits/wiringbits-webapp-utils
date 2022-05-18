package net.wiringbits.webapp.utils.admin.models

import net.wiringbits.webapp.utils.admin.utils.models.PaginationParameter
import org.scalatest.matchers.must.Matchers.{be, convertToAnyMustWrapper}
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Try

class PaginationParameterSpec extends AnyWordSpec {
  "fromString" should {
    "create a valid pagination parameter" in {
      val str = "[0,9]"
      val response = PaginationParameter.fromString(str)
      response.start must be(0)
      response.end must be(9)
    }

    "fail if the start or end of array isn't a number" in {
      val str = """["a","b"]"""
      val response = Try { PaginationParameter.fromString(str) }
      response.isFailure must be(true)
    }
  }
}
