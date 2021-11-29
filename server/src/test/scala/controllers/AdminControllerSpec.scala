package controllers

import com.dimafeng.testcontainers.PostgreSQLContainer
import controllers.common.PlayPostgresSpec
import net.wiringbits.webapp.utils.admin.AppRouter
import net.wiringbits.webapp.utils.admin.controllers.AdminController
import net.wiringbits.webapp.utils.api.models.{AdminCreateTableRequest, AdminUpdateTableRequest}
import play.api.inject.guice.GuiceApplicationBuilder

import scala.util.control.NonFatal

class AdminControllerSpec extends PlayPostgresSpec {

  override def guiceApplicationBuilder(container: PostgreSQLContainer): GuiceApplicationBuilder = {
    val appBuilder = super.guiceApplicationBuilder(container)

    val adminController = appBuilder.injector().instanceOf[AdminController]
    val appRouter = new AppRouter(adminController)
    appBuilder.router(
      appRouter
    )
  }

  "GET /admin/tables/users" should {
    "return users table" in withApiClient { client =>
      val response = client.getTableMetadata("users", 0, 10).futureValue
      response.name must be("users")
      response.fields mustNot be(empty)
    }

    "fail when you enter negative offset" in withApiClient { client =>
      val error = client
        .getTableMetadata("users", -1, 10)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue

      error must be(s"You can't query a table using negative numbers as a limit or offset")
    }

    "fail when you enter negative limit" in withApiClient { client =>
      val error = client
        .getTableMetadata("users", 0, -1)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue

      error must be(s"You can't query a table using negative numbers as a limit or offset")
    }
  }

  "GET /admin/tables/aaaaaaaaaaa" should {
    "fail when table doesn't exists" in withApiClient { client =>
      val invalidTableName = "aaaaaaaaaaa"
      val error = client
        .getTableMetadata(invalidTableName, 0, 10)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue
      error must be(s"Unexpected error because the DB table wasn't found: $invalidTableName")
    }
  }

  "POST /admin/tables/users" should {
    "create a new user" in withApiClient { client =>
      val name = "wiringbits"
      val email = "test@wiringbits.net"
      val password = "wiringbits"
      val request = AdminCreateTableRequest(Map("name" -> name, "email" -> email, "password" -> password))

      val response = client.createItem("users", request).futureValue

      response.noData must be(empty)
    }

    "fail when a mandatory field is not sent" in withApiClient { client =>
      val name = "wiringbits"
      val request = AdminCreateTableRequest(Map("name" -> name))

      val error = client
        .createItem("users", request)
        .map(_ => "Success when failure expected")
        .recover { case NonFatal(ex) =>
          ex.getMessage
        }
        .futureValue

      error must be(s"There are missing fields: email, password")
    }
  }

  "fail when field in request doesn't exists" in withApiClient { client =>
    val name = "wiringbits"
    val nonExistentField = "nonExistentField"
    val request = AdminCreateTableRequest(Map("name" -> name, "nonExistentField" -> nonExistentField))

    val error = client
      .createItem("users", request)
      .map(_ => "Success when failure expected")
      .recover { case NonFatal(ex) =>
        ex.getMessage
      }
      .futureValue

    error must be(s"A field doesn't correspond to this table schema")
  }

  "PUT /admin/tables/users" should {
    "update a new user" in withApiClient { client =>
      val name = "wiringbits"
      val email = "test@wiringbits.net"
      val password = "wiringbits"
      val request = AdminCreateTableRequest(Map("name" -> name, "email" -> email, "password" -> password))
      client.createItem("users", request).futureValue

      val getResponse = client.getTableMetadata("users", 0, 1).futureValue
      val userId = getResponse.rows.head.data.head.value

      val updateRequest = AdminUpdateTableRequest(data = Map("email" -> "wiringbits@wiringbits.net"))
      client.updateItem("users", userId, updateRequest).futureValue

      val newResponse = client.getTableMetadata("users", 0, 1).futureValue
      val newEmailOpt = newResponse.rows.head.data.find(_.value == "wiringbits@wiringbits.net")

      newEmailOpt mustNot be(None)
    }
  }

  "DELETE /admin/tables/users" should {
    "delete a new user" in withApiClient { client =>
      val name = "wiringbits"
      val email = "test@wiringbits.net"
      val password = "wiringbits"
      val request = AdminCreateTableRequest(Map("name" -> name, "email" -> email, "password" -> password))
      client.createItem("users", request).futureValue

      val getResponse = client.getTableMetadata("users", 0, 1).futureValue
      val userId = getResponse.rows.head.data.head.value

      client.deleteItem("users", userId).futureValue

      val newResponse = client.getTableMetadata("users", 0, 1).futureValue
      val usersData = newResponse.rows

      usersData.length must be(0)
    }
  }
}
