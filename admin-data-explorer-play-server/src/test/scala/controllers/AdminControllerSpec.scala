package controllers

import com.dimafeng.testcontainers.PostgreSQLContainer
import controllers.common.PlayPostgresSpec
import net.wiringbits.webapp.utils.admin.AppRouter
import net.wiringbits.webapp.utils.admin.config.{DataExplorerSettings, TableSettings}
import net.wiringbits.webapp.utils.admin.controllers.{AdminController, ImagesController}
import net.wiringbits.webapp.utils.api.models.AdminCreateTable
import org.apache.commons.lang3.StringUtils
import play.api.inject.guice.GuiceApplicationBuilder

import java.util.UUID
import java.util.regex.Pattern
import scala.util.Random

class AdminControllerSpec extends PlayPostgresSpec {
  def dataExplorerSettings: DataExplorerSettings = app.injector.instanceOf(classOf[DataExplorerSettings])
  def usersSettings: TableSettings = dataExplorerSettings.tables.headOption.value
  // TODO: loop through dataExplorerSettings for each table instead of defining usersSettings, uuidSettings
  def uuidSettings: TableSettings = dataExplorerSettings.tables(1)
  def serialSettings: TableSettings = dataExplorerSettings.tables(2)
  def bigSerialSettings: TableSettings = dataExplorerSettings.tables(3)
  def serialOverflowSettings: TableSettings = dataExplorerSettings.tables(4)
  def bigSerialOverflowSettings: TableSettings = dataExplorerSettings.tables(5)

  def isValidUUID(str: String): Boolean = {
    if (str == null) return false
    Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$").matcher(str).matches()
  }

  override def guiceApplicationBuilder(container: PostgreSQLContainer): GuiceApplicationBuilder = {
    val appBuilder = super.guiceApplicationBuilder(container)
    val adminController = appBuilder.injector().instanceOf[AdminController]
    val imagesController = appBuilder.injector().instanceOf[ImagesController]
    val appRouter = new AppRouter(adminController, imagesController)
    appBuilder.router(appRouter)
  }

  "GET /admin/tables" should {
    "return tables from modules" in withApiClient { client =>
      val response = client.getTables.futureValue
      val tableName = response.data.map(_.name).headOption.value // users
      tableName must be(usersSettings.tableName)
      val uuidTable = response.data.map(_.name)(1) // table 2
      uuidTable must be(uuidSettings.tableName)
      val serialTable = response.data.map(_.name)(2) // table 3
      serialTable must be(serialSettings.tableName)
      val bigSerialTable = response.data.map(_.name)(3) // table 4
      bigSerialTable must be(bigSerialSettings.tableName)
    }

    "return extra config from module" in withApiClient { client =>
      val response = client.getTables.futureValue
      val head = response.data.headOption.value
      head.primaryKeyName must be(usersSettings.primaryKeyField)
      usersSettings.referenceField must be(None)
      usersSettings.hiddenColumns must be(List.empty)
      usersSettings.nonEditableColumns must be(List.empty)

      val head2 = response.data(1)
      head2.primaryKeyName must be(uuidSettings.primaryKeyField)
      uuidSettings.referenceField must be(None)
      uuidSettings.hiddenColumns must be(List.empty)
      uuidSettings.nonEditableColumns must be(List.empty)

      val head3 = response.data(2)
      head3.primaryKeyName must be(serialSettings.primaryKeyField)
      serialSettings.referenceField must be(None)
      serialSettings.hiddenColumns must be(List.empty)
      serialSettings.nonEditableColumns must be(List.empty)

      val head4 = response.data(3)
      head4.primaryKeyName must be(bigSerialSettings.primaryKeyField)
      bigSerialSettings.referenceField must be(None)
      bigSerialSettings.hiddenColumns must be(List.empty)
      bigSerialSettings.nonEditableColumns must be(List.empty)
    }
  }

  "GET /admin/tables/:tableName" should {
    "return data" in withApiClient { client =>
      val name = "wiringbits"
      val email = "test@wiringbits.net"
      val request = AdminCreateTable.Request(
        Map("name" -> name, "email" -> email, "password" -> "wiringbits")
      )
      client.createItem(usersSettings.tableName, request).futureValue

      val response = client.getTableMetadata(usersSettings.tableName, List("name", "ASC"), List(0, 9), "{}").futureValue
      val head = response.headOption.value
      // TODO: Find a better way to do this
      val nameValue = head.find(_._1 == "name").value._2
      val emailValue = head.find(_._1 == "email").value._2
      response.size must be(1)
      name must be(nameValue)
      email must be(emailValue)
    }

    "return data from uuid table" in withApiClient { client =>
      val name = "wiringbits"
      // val uuid_id =  UUID.randomUUID().toString
      val request = AdminCreateTable.Request(
        Map("name" -> name)
      )
      client.createItem(uuidSettings.tableName, request).futureValue

      val response = client.getTableMetadata(uuidSettings.tableName, List("name", "ASC"), List(0, 9), "{}").futureValue
      val head = response.headOption.value
      // TODO: Find a better way to do this
      val uuidValue = head.find(_._1 == "id").value._2
      val nameValue = head.find(_._1 == "name").value._2
      response.size must be(1)
      name must be(nameValue)
      isValidUUID(uuidValue) must be(true)
    }

    "return data from serial table" in withApiClient { client =>
      val name = "wiringbits"
      val request = AdminCreateTable.Request(Map("name" -> name))
      client.createItem(serialSettings.tableName, request).futureValue

      val response =
        client.getTableMetadata(serialSettings.tableName, List("name", "ASC"), List(0, 9), "{}").futureValue
      val head = response.headOption.value
      // TODO: Find a better way to do this
      val intValue = head.find(_._1 == "id").value._2
      val nameValue = head.find(_._1 == "name").value._2
      response.size must be(1)
      intValue must be("1")
      name must be(nameValue)
    }
    "return data from big serial table" in withApiClient { client =>
      val name = "wiringbits"
      val request = AdminCreateTable.Request(Map("name" -> name))
      client.createItem(bigSerialSettings.tableName, request).futureValue

      val response =
        client.getTableMetadata(bigSerialSettings.tableName, List("name", "ASC"), List(0, 9), "{}").futureValue
      val head = response.headOption.value
      // TODO: Find a better way to do this
      val intValue = head.find(_._1 == "id").value._2
      val nameValue = head.find(_._1 == "name").value._2
      response.size must be(1)
      intValue must be("1")
      name must be(nameValue)
    }

    "return a empty map if there isn't any user" in withApiClient { client =>
      val response = client.getTableMetadata(usersSettings.tableName, List("name", "ASC"), List(0, 9), "{}").futureValue
      response.size must be(0)
    }

    "return an empty map if tables are empty" in withApiClient { client =>
      val response = client.getTableMetadata(uuidSettings.tableName, List("id", "ASC"), List(0, 9), "{}").futureValue
      response.size must be(0)
      val response2 = client.getTableMetadata(serialSettings.tableName, List("id", "ASC"), List(0, 9), "{}").futureValue
      response2.size must be(0)
      val response3 =
        client.getTableMetadata(bigSerialSettings.tableName, List("id", "ASC"), List(0, 9), "{}").futureValue
      response3.size must be(0)
    }

    "return a empty map if start and end is the same" in withApiClient { client =>
      val request = AdminCreateTable.Request(
        Map("name" -> "wiringbits", "email" -> "test@wiringbits.net", "password" -> "wiringbits")
      )
      client.createItem(usersSettings.tableName, request).futureValue

      val response = client.getTableMetadata(usersSettings.tableName, List("name", "ASC"), List(0, 0), "{}").futureValue
      response.size must be(0)
    }

    "return an empty map for range of zero length" in withApiClient { client =>
      val name = "wiringbits"
      val request = AdminCreateTable.Request(
        Map("name" -> name)
      ) // can use this for all 3 of the simple tables UUID, SERIAL, BIGSERIAL

      client.createItem(uuidSettings.tableName, request).futureValue // 1
      val response1 = client.getTableMetadata(uuidSettings.tableName, List("id", "ASC"), List(0, 0), "{}").futureValue
      response1.size must be(0)

      client.createItem(serialSettings.tableName, request).futureValue // 2
      val response2 = client.getTableMetadata(serialSettings.tableName, List("id", "ASC"), List(0, 0), "{}").futureValue
      response2.size must be(0)

      client.createItem(bigSerialSettings.tableName, request).futureValue // 2
      val response3 =
        client.getTableMetadata(bigSerialSettings.tableName, List("id", "ASC"), List(0, 0), "{}").futureValue
      response3.size must be(0)

    }

    "only return the end minus start elements" in withApiClient { client =>
      Range.apply(0, 4).foreach { i =>
        val data = Map("name" -> "wiringbits", "email" -> s"test@wiringbits$i.net", "password" -> "wiringbits")
        val request = AdminCreateTable.Request(data)
        client.createItem(usersSettings.tableName, request).futureValue
      }
      val end = 2
      val start = 1
      val returnedElements = end - start
      val response =
        client.getTableMetadata(usersSettings.tableName, List("name", "ASC"), List(start, end), "{}").futureValue
      response.size must be(returnedElements)
    }

    "only return the range size of elements" in withApiClient { client =>
      val end = 2
      val start = 1
      val returnedElements = end - start
      // val data = Map() // data for these tables is always nothing. BIG/SERIAL autogenerated.
      val name = "wiringbits"

      val tables = List(uuidSettings, serialSettings, bigSerialSettings)
      // this could just be a for loop instead of for comprehension
      for (table <- tables)
        yield {
          Range.apply(0, 4).foreach { _ =>
            val request = AdminCreateTable.Request(
              Map("name" -> name)
            ) // tried using data and got error. I thought val data was in scope but perhaps not
            client.createItem(table.tableName, request).futureValue
          }
        }

      for (table <- tables)
        yield {
          val response = client.getTableMetadata(table.tableName, List("id", "ASC"), List(start, end), "{}").futureValue
          response.size must be(returnedElements)
        }
    }

    "return the elements in ascending order" in withApiClient { client =>
      // should really be 5, since 5 users are created. Or Range.apply should start at 1.
      val createdUsers = 4
      val nameLength = 7
      Range.apply(0, createdUsers).foreach { i =>
        val letter = Character.valueOf(('A' + i).toChar)
        val name = StringUtils.repeat(letter, nameLength)
        val data = Map("name" -> name, "email" -> s"test@wiringbits$i.net", "password" -> "wiringbits")
        val request = AdminCreateTable.Request(data)
        client.createItem(usersSettings.tableName, request).futureValue
      }
      val response =
        client.getTableMetadata(usersSettings.tableName, List("name", "ASC"), List(0, createdUsers), "{}").futureValue
      val head = response.headOption.value
      val name = head.find(_._1 == "name").value._2
      response.size must be(createdUsers)
      name must be(StringUtils.repeat('A', nameLength))
    }

    // TODO: add uuidSettings table to tests below
    "return the elements of all tables in ascending order" in withApiClient { client =>
      val createdRows = 4
      val tables = List(serialSettings, bigSerialSettings)
      val name = "wiringbits"
      val expectedName = name + "0"

      // insert rows
      for (table <- tables)
        yield {
          Range.apply(0, createdRows).foreach { i =>
            val request = AdminCreateTable.Request(Map("name" -> s"$name$i"))
            client.createItem(table.tableName, request).futureValue
          }
        }

      for (table <- tables)
        yield {
          val response =
            client.getTableMetadata(table.tableName, List("name", "ASC"), List(0, createdRows), "{}").futureValue
          val head = response.headOption.value
          val id = head.find(_._1 == "id").value._2 // id from response
          val nameValue = head.find(_._1 == "name").value._2 // name from response
          response.size must be(createdRows)
          id must be("1")
          nameValue must be(expectedName)
        }
    }

    "return the elements in descending order" in withApiClient { client =>
      val createdUsers = 4
      val nameLength = 7
      Range.apply(0, createdUsers).foreach { i =>
        val letter = Character.valueOf(('A' + i).toChar)
        val name = StringUtils.repeat(letter, nameLength);
        val data = Map("name" -> name, "email" -> s"test@wiringbits$i.net", "password" -> "wiringbits")
        val request = AdminCreateTable.Request(data)
        client.createItem(usersSettings.tableName, request).futureValue
      }
      val response =
        client.getTableMetadata(usersSettings.tableName, List("name", "DESC"), List(0, createdUsers), "{}").futureValue
      val head = response.headOption.value
      val name = head.find(_._1 == "name").value._2
      response.size must be(createdUsers)
      name must be(StringUtils.repeat('D', nameLength))
    }
    "return the elements of all tables in descending order" in withApiClient { client =>
      val createdRows = 4
      // removed uuidSettings because ordering is random
      val tables = List(serialSettings, bigSerialSettings)
      val name = "Johnny"
      val expectedNameValue = name + (createdRows - 1).toString
      // insert rows
      for (table <- tables)
        yield {
          Range.apply(0, createdRows).foreach { i =>
            val request = AdminCreateTable.Request(Map("name" -> s"$name$i"))
            client.createItem(table.tableName, request).futureValue
          }
        }

      for (table <- tables)
        yield {
          val response =
            client.getTableMetadata(table.tableName, List("name", "DESC"), List(0, createdRows), "{}").futureValue
          val head = response.headOption.value
          val id = head.find(_._1 == "id").value._2 // id from response
          val nameValue = head.find(_._1 == "name").value._2 // name from response
          response.size must be(createdRows)
          id must be(createdRows.toString) // toString
          expectedNameValue must be(nameValue)
        }
    }

    "return filtered elements" in withApiClient { client =>
      val createdUsers = 4
      Range.apply(0, createdUsers).foreach { i =>
        val data = Map("name" -> "wiringbits", "email" -> s"test@wiringbits$i.net", "password" -> "wiringbits")
        val request = AdminCreateTable.Request(data)
        client.createItem(usersSettings.tableName, request).futureValue
      }
      val expectedEmail = "test@wiringbits0.net"
      val response =
        client
          .getTableMetadata(
            usersSettings.tableName,
            List("name", "ASC"),
            List(0, createdUsers),
            s"""{"email":"$expectedEmail"}"""
          )
          .futureValue

      val head = response.headOption.value
      val email = head.find(_._1 == "email").value._2
      response.size must be(1)
      email must be(expectedEmail)
    }

    "return filtered elements for all tables" in withApiClient { client =>
      val createdRows = 4
      val tables = List(serialSettings, bigSerialSettings)

      for (table <- tables) {
        Range.apply(0, createdRows).foreach { i =>
          val data = Map("name" -> s"wiringbits$i")
          val request = AdminCreateTable.Request(data)
          client.createItem(table.tableName, request).futureValue
        }
      }
      val expectedName = "wiringbits0"

      for (table <- tables) {
        val response =
          client
            .getTableMetadata(
              table.tableName,
              List("name", "ASC"),
              List(0, createdRows),
              s"""{"name":"$expectedName"}"""
            )
            .futureValue

        val head = response.headOption.value
        val name = head.find(_._1 == "name").value._2
        response.size must be(1)
        name must be(expectedName)
      }
    }

    "fail when table doesn't exists" in withApiClient { client =>
      val invalidTableName = "aaaaaaaaaaa"
      val error = client.getTableMetadata(invalidTableName, List("name", "ASC"), List(0, 9), "{}").expectError
      error must be(s"Unexpected error because the DB table wasn't found: $invalidTableName")
    }

    "return data with partial match" in withApiClient { client =>
      val name = "wiringbits"
      val email = "test@wiringbits.net"
      val request = AdminCreateTable.Request(
        Map("name" -> name, "email" -> email, "password" -> "wiringbits")
      )
      client.createItem(usersSettings.tableName, request).futureValue

      val response = client
        .getTableMetadata(usersSettings.tableName, List("name", "ASC"), List(0, 9), """{"name":"irin"}""")
        .futureValue
      val head = response.headOption.value
      val nameValue = head.find(_._1 == "name").value._2
      val emailValue = head.find(_._1 == "email").value._2
      response.size must be(1)
      name must be(nameValue)
      email must be(emailValue)
    }

    "not return data due to partial match" in withApiClient { client =>
      val name = "wiringbits"
      val email = "test@wiringbits.net"
      val request = AdminCreateTable.Request(
        Map("name" -> name, "email" -> email, "password" -> "wiringbits")
      )
      client.createItem(usersSettings.tableName, request).futureValue

      val response = client
        .getTableMetadata(usersSettings.tableName, List("name", "ASC"), List(0, 9), """{"name":"yyy"}""")
        .futureValue
      response.headOption.isEmpty must be(true)
    }

    "return data with two filters" in withApiClient { client =>
      val name = "wiringbits"
      val email = "test@wiringbits.net"
      val request = AdminCreateTable.Request(
        Map("name" -> name, "email" -> email, "password" -> "wiringbits")
      )
      client.createItem(usersSettings.tableName, request).futureValue

      val response = client
        .getTableMetadata(
          usersSettings.tableName,
          List("name", "ASC"),
          List(0, 9),
          """{"name":"irin","email":"test"}"""
        )
        .futureValue
      val head = response.headOption.value
      val nameValue = head.find(_._1 == "name").value._2
      val emailValue = head.find(_._1 == "email").value._2
      response.size must be(1)
      name must be(nameValue)
      email must be(emailValue)
    }

    "not return with a valid filter and a non valid filter" in withApiClient { client =>
      val name = "wiringbits"
      val email = "test@wiringbits.net"
      val request = AdminCreateTable.Request(
        Map("name" -> name, "email" -> email, "password" -> "wiringbits")
      )
      client.createItem(usersSettings.tableName, request).futureValue

      val response = client
        .getTableMetadata(
          usersSettings.tableName,
          List("name", "ASC"),
          List(0, 9),
          """{"name":"irin","email":"yyyy"}"""
        )
        .futureValue
      response.headOption.isEmpty must be(true)
    }
  }

  "GET /admin/tables/:tableName/:primaryKey" should {
    "return table row" in withApiClient { client =>
      val name = "wiringbits"
      val email = "test@wiringbits.net"
      val password = "wiringbits"
      val request = AdminCreateTable.Request(Map("name" -> name, "email" -> email, "password" -> password))
      client.createItem(usersSettings.tableName, request).futureValue

      val users = client.getTableMetadata(usersSettings.tableName, List("name", "ASC"), List(0, 9), "{}").futureValue
      val userId = users.headOption.value.find(_._1 == "id").value._2

      val response = client.viewItem(usersSettings.tableName, userId).futureValue
      response.find(_._1 == "name").value._2 must be(name)
      response.find(_._1 == "email").value._2 must be(email)
      response.find(_._1 == "password").value._2 must be(password)
      response.find(_._1 == "id").value._2 must be(userId)
    }

    "return table row for all tables" in withApiClient { client =>
      val tables = List(serialSettings, bigSerialSettings)
      val name = "wiringbits"
      val request = AdminCreateTable.Request(Map("name" -> name))

      for (table <- tables) {
        client.createItem(table.tableName, request).futureValue

        val rows = client.getTableMetadata(table.tableName, List("name", "ASC"), List(0, 9), "{}").futureValue
        val rowId = rows.headOption.value.find(_._1 == "id").value._2

        val response = client.viewItem(table.tableName, rowId).futureValue
        response.find(_._1 == "name").value._2 must be(name)
        response.find(_._1 == "id").value._2 must be(rowId)
      }
    }

    "fail if the table row doesn't exists" in withApiClient { client =>
      val userId = UUID.randomUUID().toString
      val error = client.viewItem(usersSettings.tableName, userId).expectError
      error must be(s"Cannot find item in users with id $userId")
    }

    "fail if the table row doesn't exists for all tables" in withApiClient { client =>
      val tables = List(serialSettings, bigSerialSettings)
      val rand = new Random()
      val id = rand.nextInt(1000) + 100
      for (table <- tables) {
        val tableName = table.tableName
        val error = client.viewItem(table.tableName, id.toString).expectError
        error must be(s"Cannot find item in $tableName with id $id")
      }
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

    "return table rows for all tables" in withApiClient { client =>
      val tables = List(serialSettings, bigSerialSettings)
      val numOfCreatedRows = 3
      for (table <- tables) {
        Range.apply(0, numOfCreatedRows).foreach { i =>
          val request = AdminCreateTable
            .Request(Map("name" -> s"wiringbits$i"))
          client.createItem(table.tableName, request).futureValue
        }
        val rows = client.getTableMetadata(table.tableName, List("name", "ASC"), List(0, 9), "{}").futureValue
        val ids = rows.map(_.find(_._1 == "id").value._2)
        val response = client.viewItems(table.tableName, ids).futureValue
        // TODO: check the names
        // val sameName = response.flatMap(_.find(_._1 == "name")).forall(_._2 == "wiringbits")

        response.size must be(ids.length)
        response.size must be(numOfCreatedRows)
        /*sameName must be(true)
        samePassword must be(true)*/
      }
    }

    "fail if the table row doesn't exists" in withApiClient { client =>
      val userIds = List(UUID.randomUUID().toString)
      val error = client.viewItems("users", userIds).expectError
      error must be(s"Cannot find item in users with id ${userIds.headOption.value}")
    }

    "fail if the table row doesn't exists for all tables" in withApiClient { client =>
      val tables = List(serialSettings, bigSerialSettings)
      val rand = new Random()
      val ids = List((rand.nextInt(1000) + 100).toString)
      for (table <- tables) {
        val tableName = table.tableName
        val error = client.viewItems(tableName, ids).expectError
        error must be(s"Cannot find item in $tableName with id ${ids.headOption.value}")
      }
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

    "create a new row for all tables" in withApiClient { client =>
      val tables = List(serialSettings, bigSerialSettings)
      val name = "wiringbits"

      for (table <- tables) {
        val request = AdminCreateTable.Request(Map("name" -> name))
        val response = client.createItem(table.tableName, request).futureValue
        response.noData must be(empty)
      }
    }

    "fail when a mandatory field is not sent" in withApiClient { client =>
      val name = "wiringbits"
      val request = AdminCreateTable.Request(Map("name" -> name))

      val error = client.createItem("users", request).expectError
      error must be(s"There are missing fields: email, password")
    }

    "fail when a mandatory field is not sent for all tables" in withApiClient { client =>
      val tables = List(serialSettings, bigSerialSettings)
      // val name = "wiringbits"
      for (table <- tables) {
        val request = AdminCreateTable.Request(Map())
        val error = client.createItem(table.tableName, request).expectError
        error must be(s"There are missing fields: name")
      }
    }

    "fail when overlow max int value" in withApiClient { client =>
      val name = "wiringbits"
      val table = serialOverflowSettings
      val request = AdminCreateTable.Request(Map("name" -> name))
      val ignore = client.createItem(table.tableName, request).futureValue
      ignore.noData must be(empty)
      val request2 = AdminCreateTable.Request(Map("name" -> s"asdf"))
      val error = client.createItem(table.tableName, request2).expectError
      error must be(s"ERROR: integer out of range")
    }
    "fail when overlow max bigint value" in withApiClient { client =>
      val name = "wiringbits"
      val table = bigSerialOverflowSettings
      val request = AdminCreateTable.Request(Map("name" -> name))
      val ignore = client.createItem(table.tableName, request).futureValue
      ignore.noData must be(empty)
      val request2 = AdminCreateTable.Request(Map("name" -> s"asdf"))
      val error = client.createItem(table.tableName, request2).expectError
      error must be(
        s"ERROR: nextval: reached maximum value of sequence \"big_serial_table_overflow_seq\" (9223372036854775807)"
      )
    }
  }

  "fail when field in request doesn't exists" in withApiClient { client =>
    val name = "wiringbits"
    val nonExistentField = "nonExistentField"
    val request = AdminCreateTable.Request(Map("name" -> name, "nonExistentField" -> nonExistentField))

    val error = client.createItem("users", request).expectError
    error must be(s"A field doesn't correspond to this table schema")
  }
  "fail when field in request doesn't exists for all tables" in withApiClient { client =>
    val tables = List(serialSettings, bigSerialSettings)
    val name = "wiringbits"
    val nonExistentField = "nonExistentField"
    for (table <- tables) {
      val request = AdminCreateTable.Request(Map("name" -> name, "nonExistentField" -> nonExistentField))

      val error = client.createItem(table.tableName, request).expectError
      error must be(s"A field doesn't correspond to this table schema")
    }
  }

  "PUT /admin/tables/:tableName" should {
    "update a new user" in withApiClient { client =>
      val request = AdminCreateTable.Request(
        Map("name" -> "wiringbits", "email" -> "test@wiringbits.net", "password" -> "wiringbits")
      )
      client.createItem(usersSettings.tableName, request).futureValue

      val response = client.getTableMetadata(usersSettings.tableName, List("name", "ASC"), List(0, 9), "{}").futureValue
      val userId = response.headOption.value.find(_._1 == "id").value._2

      val email = "wiringbits@wiringbits.net"
      val updateRequest = Map("email" -> email)
      val updateResponse = client.updateItem(usersSettings.tableName, userId, updateRequest).futureValue

      val newResponse = client.viewItem(usersSettings.tableName, userId).futureValue
      val emailResponse = newResponse.find(_._1 == "email").value._2
      updateResponse.id must be(userId)
      emailResponse must be(email)
    }

    "update a new row for all tables" in withApiClient { client =>
      val tables = List(serialSettings, bigSerialSettings)
      for (table <- tables) {
        val request = AdminCreateTable.Request(
          Map("name" -> "wiringbits")
        )
        client.createItem(table.tableName, request).futureValue
        val response = client.getTableMetadata(table.tableName, List("name", "ASC"), List(0, 9), "{}").futureValue
        val id = response.headOption.value.find(_._1 == "id").value._2

        val name = "wiringbitsbitsbits"
        val updateRequest = Map("name" -> name)
        val updateResponse = client.updateItem(table.tableName, id, updateRequest).futureValue

        val newResponse = client.viewItem(table.tableName, id).futureValue
        val nameResponse = newResponse.find(_._1 == "name").value._2
        updateResponse.id must be(id)
        nameResponse must be(name)
      }
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

    "fail if the field in body doesn't exists for all tables" in withApiClient { client =>
      val tables = List(serialSettings, bigSerialSettings)
      for (table <- tables) {
        val request = AdminCreateTable.Request(
          Map("name" -> "test")
        )
        client.createItem(table.tableName, request).futureValue

        val response = client.getTableMetadata(table.tableName, List("id", "ASC"), List(0, 9), "{}").futureValue
        val id = response.headOption.value.find(_._1 == "id").value._2

        val name = "wiringbits"
        val updateRequest = Map("nonExistentField" -> name)
        val error = client.updateItem(table.tableName, id, updateRequest).expectError
        error must be("A field doesn't correspond to this table schema")
      }
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

  "DELETE /admin/tables/:tableName" should {
    "delete a new row in all tables" in withApiClient { client =>
      val tables = List(serialSettings, bigSerialSettings)
      val name = "wiringbits"
      val request = AdminCreateTable.Request(Map("name" -> name))

      for (table <- tables) {
        client.createItem(table.tableName, request).futureValue

        val response = client.getTableMetadata(table.tableName, List("name", "ASC"), List(0, 9), "{}").futureValue
        val id = response.headOption.value.find(_._1 == "id").value._2

        client.deleteItem(table.tableName, id).futureValue

        val newResponse = client.getTableMetadata(table.tableName, List("name", "ASC"), List(0, 9), "{}").futureValue
        newResponse.isEmpty must be(true)
      }
    }
  }
}
