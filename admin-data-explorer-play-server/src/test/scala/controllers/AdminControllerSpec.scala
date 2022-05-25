package controllers

import com.dimafeng.testcontainers.PostgreSQLContainer
import controllers.common.PlayPostgresSpec
import net.wiringbits.webapp.utils.admin.AppRouter
import net.wiringbits.webapp.utils.admin.controllers.AdminController
import net.wiringbits.webapp.utils.api.models.AdminCreateTable
import play.api.inject.guice.GuiceApplicationBuilder

import java.util.UUID

class AdminControllerSpec extends PlayPostgresSpec {

  override def guiceApplicationBuilder(container: PostgreSQLContainer): GuiceApplicationBuilder = {
    val appBuilder = super.guiceApplicationBuilder(container)

    val adminController = appBuilder.injector().instanceOf[AdminController]
    val appRouter = new AppRouter(adminController)
    appBuilder.router(appRouter)
  }

  "GET /admin/tables" should {
    "return tables from modules" in withApiClient { client =>
      val response = client.getTables.futureValue
      val tableName = response.data.map(_.name).headOption.value
      tableName must be("users")
    }
  }

  "GET /admin/tables/:tableName" should {
    "return users table" in withApiClient { client =>
      val response = client.getTableMetadata("users", List("name", "ASC"), List(0, 9), "{}").futureValue
      response.size must be(0)
    }

    "fail when table doesn't exists" in withApiClient { client =>
      val invalidTableName = "aaaaaaaaaaa"
      val error = client.getTableMetadata(invalidTableName, List("name", "ASC"), List(0, 9), "{}").expectError
      error must be(s"Unexpected error because the DB table wasn't found: $invalidTableName")
    }
  }

  "GET /admin/tables/:tableName/:primaryKey" should {
    "return table row" in withApiClient { client =>
      val tableName = "users"
      val name = "wiringbits"
      val email = "test@wiringbits.net"
      val password = "wiringbits"
      val request = AdminCreateTable.Request(Map("name" -> name, "email" -> email, "password" -> password))
      client.createItem(tableName, request).futureValue

      val users = client.getTableMetadata(tableName, List("name", "ASC"), List(0, 9), "{}").futureValue
      val userId = users.headOption.value.find(_._1 == "id").value._2

      val response = client.viewItem(tableName, userId).futureValue
      response.find(_._1 == "name").value._2 must be(name)
      response.find(_._1 == "email").value._2 must be(email)
      response.find(_._1 == "password").value._2 must be(password)
      response.find(_._1 == "id").value._2 must be(userId)
    }

    "fail if the table row doesn't exists" in withApiClient { client =>
      val userId = UUID.randomUUID().toString
      val error = client.viewItem("users", userId).expectError
      error must be(s"Cannot find item in users with id $userId")
    }
  }

  "GET /admin/tables/:tableName/:primaryKeys" should {
    "return table rows" in withApiClient { client =>
      val tableName = "users"
      val numOfCreatedUsers = 3
      Range.apply(0, numOfCreatedUsers).foreach { i =>
        val request = AdminCreateTable
          .Request(Map("name" -> "wiringbits", "email" -> s"test@wiringbits$i.net", "password" -> "wiringbits"))
        client.createItem(tableName, request).futureValue
      }
      val users = client.getTableMetadata(tableName, List("name", "ASC"), List(0, 9), "{}").futureValue
      val userIds = users.map(_.find(_._1 == "id").value._2)
      val response = client.viewItems(tableName, userIds).futureValue
      val sameName = response.flatMap(_.find(_._1 == "name")).forall(_._2 == "wiringbits")
      val samePassword = response.flatMap(_.find(_._1 == "password")).forall(_._2 == "wiringbits")

      response.size must be(userIds.length)
      response.size must be(numOfCreatedUsers)
      sameName must be(true)
      samePassword must be(true)
    }

    "fail if the table row doesn't exists" in withApiClient { client =>
      val userIds = List(UUID.randomUUID().toString)
      val error = client.viewItems("users", userIds).expectError
      error must be(s"Cannot find item in users with id ${userIds.headOption.value}")
    }
  }

  "POST /admin/tables/:tableName" should {
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

  "PUT /admin/tables/:tableName" should {
    "update a new user" in withApiClient { client =>
      val request = AdminCreateTable.Request(
        Map("name" -> "wiringbits", "email" -> "test@wiringbits.net", "password" -> "wiringbits")
      )
      client.createItem("users", request).futureValue

      val response = client.getTableMetadata("users", List("user_id", "ASC"), List(0, 9), "{}").futureValue
      val userId = response.headOption.value.find(_._1 == "id").value._2

      val email = "wiringbits@wiringbits.net"
      val updateRequest = Map("email" -> email)
      val updateResponse = client.updateItem("users", userId, updateRequest).futureValue

      val newResponse = client.viewItem("users", userId).futureValue
      val emailResponse = newResponse.find(_._1 == "email").value._2
      updateResponse.id must be(userId)
      emailResponse must be(email)
    }

    "fail if the field in body doesn't exists" in withApiClient { client =>
      val request = AdminCreateTable.Request(
        Map("name" -> "wiringbits", "email" -> "test@wiringbits.net", "password" -> "wiringbits")
      )
      client.createItem("users", request).futureValue

      val response = client.getTableMetadata("users", List("user_id", "ASC"), List(0, 9), "{}").futureValue
      val userId = response.headOption.value.find(_._1 == "id").value._2

      val email = "wiringbits@wiringbits.net"
      val updateRequest = Map("nonExistentField" -> email)
      val error = client.updateItem("users", userId, updateRequest).expectError
      error must be("A field doesn't correspond to this table schema")
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
