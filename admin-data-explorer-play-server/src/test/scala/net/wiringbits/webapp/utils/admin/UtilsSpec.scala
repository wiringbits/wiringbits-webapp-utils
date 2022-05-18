package net.wiringbits.webapp.utils.admin

import net.wiringbits.webapp.utils.admin.utils.StringToDataTypesExt
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.matchers.must.Matchers.{be, convertToAnyMustWrapper}
import org.scalatest.wordspec.AnyWordSpec

class UtilsSpec extends AnyWordSpec {
  "toStringList" should {
    "create a list" in {
      val response = """["a","b"]""".toStringList
      response.length must be(2)
      response.contains("a") must be(true)
      response.contains("b") must be(true)
    }
  }

  "toStringMap" should {
    "create a empty map" in {
      val response = "{}".toStringMap
      response.size must be(0)
    }

    "create a new map from str" in {
      val response = """{"a":"b"}""".toStringMap
      val head = response.headOption.value
      response.size must be(1)
      head._1 must be("a")
      head._2 must be("b")
    }
  }
}
