package controllers

import com.dimafeng.testcontainers.PostgreSQLContainer
import controllers.common.PlayPostgresSpec
import net.wiringbits.webapp.utils.admin.AppRouter
import net.wiringbits.webapp.utils.admin.controllers.AdminController
import net.wiringbits.webapp.utils.api.models.AdminCreateTable
import play.api.inject.guice.GuiceApplicationBuilder

class AdminControllerSpec extends PlayPostgresSpec {

  override def guiceApplicationBuilder(container: PostgreSQLContainer): GuiceApplicationBuilder = {
    val appBuilder = super.guiceApplicationBuilder(container)

    val adminController = appBuilder.injector().instanceOf[AdminController]
    val appRouter = new AppRouter(adminController)
    appBuilder.router(appRouter)
  }

  "GET /admin/tables/users" should {
    "return users table" in withApiClient { client =>
      val response = client.getTableMetadata("users", List("name", "ASC"), List(0, 9), "{}").futureValue
      response.size must be(0)
    }
  }

  "GET /admin/tables/aaaaaaaaaaa" should {
    "fail when table doesn't exists" in withApiClient { client =>
      val invalidTableName = "aaaaaaaaaaa"
      val error = client.getTableMetadata(invalidTableName, List("name", "ASC"), List(0, 9), "{}").expectError
      error must be(s"Unexpected error because the DB table wasn't found: $invalidTableName")
    }
  }

  "POST /admin/tables/users" should {
    "create a new user" in withApiClient { client =>
      val name = "wiringbits"
      val email = "test@wiringbits.net"
      val password = "wiringbits"
      val request = AdminCreateTable.Request(Map("name" -> name, "email" -> email, "password" -> password))
      val response = client.createItem("users", request).futureValue
      response.noData must be(empty)
    }

    "fail when a mandatory field is not sent" in withApiClient { client =>
      val name = "wiringbits"
      val request = AdminCreateTable.Request(Map("name" -> name))

      val error = client.createItem("users", request).expectError
      error must be(s"There are missing fields: email, password")
    }
  }

  "fail when field in request doesn't exists" in withApiClient { client =>
    val name = "wiringbits"
    val nonExistentField = "nonExistentField"
    val request = AdminCreateTable.Request(Map("name" -> name, "nonExistentField" -> nonExistentField))

    val error = client.createItem("users", request).expectError
    error must be(s"A field doesn't correspond to this table schema")
  }

  "PUT /admin/tables/users" should {
    "update a new user" in withApiClient { client =>
      val request = AdminCreateTable.Request(
        Map("name" -> "wiringbits", "email" -> "test@wiringbits.net", "password" -> "wiringbits")
      )
      client.createItem("users", request).futureValue

      val response = client.getTableMetadata("users", List("user_id", "ASC"), List(0, 9), "{}").futureValue
      val userId = response.headOption.value.find(_._1 == "id").value._2
      println(response)
      println(userId)

      val email = "wiringbits@wiringbits.net"
      val updateRequest = Map("email" -> email)
      client.updateItem("users", userId, updateRequest).futureValue

      val newResponse = client.viewItem("users", userId).futureValue
      val emailResponse = newResponse.find(_._1 == "email").value._2
      emailResponse must be(email)
    }
  }

  "DELETE /admin/tables/users" should {
    "delete a new user" in withApiClient { client =>
      val name = "wiringbits"
      val email = "test@wiringbits.net"
      val password = "wiringbits"
      val request = AdminCreateTable.Request(Map("name" -> name, "email" -> email, "password" -> password))
      client.createItem("users", request).futureValue

      val response = client.getTableMetadata("users", List("name", "ASC"), List(0, 9), "{}").futureValue
      val userId = response.headOption.value.find(_._1 == "id").value._2

      client.deleteItem("users", userId).futureValue

      val newResponse = client.getTableMetadata("users", List("name", "ASC"), List(0, 9), "{}").futureValue
      newResponse.isEmpty must be(true)
    }
  }
}
