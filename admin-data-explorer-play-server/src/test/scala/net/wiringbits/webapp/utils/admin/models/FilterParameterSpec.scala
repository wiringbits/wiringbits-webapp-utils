package net.wiringbits.webapp.utils.admin.models

import net.wiringbits.webapp.utils.admin.utils.models.FilterParameter
import org.scalatest.matchers.must.Matchers.{be, convertToAnyMustWrapper}
import org.scalatest.wordspec.AnyWordSpec

class FilterParameterSpec extends AnyWordSpec {
  "fromString" should {
    "create a valid filter parameter" in {
      val str = """{"name": "wiringbits@wiringbits.net"}"""
      val response = FilterParameter.fromString(str)
      response.head.field must be("name")
      response.head.value must be("wiringbits@wiringbits.net")
    }

    "create a empty model if str is a empty json" in {
      val str = "{}"
      val response = FilterParameter.fromString(str)
      response.isEmpty must be(true)
    }

    "create a valid filter parameter with two items" in {
      val str = """{"name": "wiringbits@wiringbits.net", "password": "wiringbits"}"""
      val response = FilterParameter.fromString(str)
      response.exists(item => item.field == "name" && item.value == "wiringbits@wiringbits.net") must be(true)
      response.exists(item => item.field == "password" && item.value == "wiringbits") must be(true)
    }
  }
}
