package net.wiringbits.webapp.utils.admin.models

import net.wiringbits.webapp.utils.admin.utils.models.FilterParameter
import org.scalatest.matchers.must.Matchers.{be, convertToAnyMustWrapper}
import org.scalatest.wordspec.AnyWordSpec

class FilterParameterSpec extends AnyWordSpec {
  "fromString" should {
    "create a valid filter parameter" in {
      val str = """{"name": "wiringbits@wiringbits.net"}"""
      val response = FilterParameter.fromString(str)
      response.field must be("name")
      response.value must be("wiringbits@wiringbits.net")
    }

    "create a empty model if str is a empty json" in {
      val str = "{}"
      val response = FilterParameter.fromString(str)
      response.field must be("")
      response.value must be("")
    }
  }
}
