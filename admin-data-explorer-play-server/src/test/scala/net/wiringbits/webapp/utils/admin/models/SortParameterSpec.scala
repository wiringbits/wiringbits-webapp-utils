package net.wiringbits.webapp.utils.admin.models

import net.wiringbits.webapp.utils.admin.utils.models.SortParameter
import org.scalatest.matchers.must.Matchers.{be, convertToAnyMustWrapper}
import org.scalatest.wordspec.AnyWordSpec

class SortParameterSpec extends AnyWordSpec {
  "fromString" should {
    "create a valid sort parameter" in {
      val str = """["name","ASC"]"""
      // val primaryKeyField = "user_id"
      val response = SortParameter.fromString(str)
      response.field must be("name")
      response.ordering must be("ASC")
    }
  }
  "fromPrimaryKeyField" should {
    "convert id property to primary key field" in {
      val str = """["id","ASC"]"""
      val primaryKeyField = "user_id"
      val response = SortParameter.fromString(str)
      val queryParams = response.fromPrimaryKeyField(primaryKeyField)
      queryParams.field must be("user_id")
      queryParams.ordering must be("ASC")
    }
  }
}
