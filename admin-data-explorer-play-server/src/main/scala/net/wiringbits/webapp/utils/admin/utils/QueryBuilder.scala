package net.wiringbits.webapp.utils.admin.utils

import net.wiringbits.webapp.utils.admin.repositories.models.TableColumn

import scala.collection.mutable

object QueryBuilder {
  def create(tableName: String, body: Map[String, String], primaryKeyField: String): String = {
    val sqlFields = new mutable.StringBuilder(primaryKeyField)
    val sqlValues = new mutable.StringBuilder("?")
    for ((key, _) <- body) {
      sqlFields.append(s", $key")
      sqlValues.append(s", ?")
    }

    s"""
      |INSERT INTO $tableName
      |  ($sqlFields)
      |VALUES (
      |  ${sqlValues.toString()}
      |)
      |""".stripMargin
  }

  def update(tableName: String, body: Map[TableColumn, String], primaryKeyField: String): String = {
    val updateStatement = new mutable.StringBuilder("SET")
    for ((tableField, value) <- body) {
      val resultStatement = if (value == "null") "NULL" else s"?::${tableField.`type`}"
      val statement = s" ${tableField.name} = $resultStatement,"
      updateStatement.append(statement)
    }
    updateStatement.deleteCharAt(updateStatement.length - 1)
    s"""
    |UPDATE $tableName
    |$updateStatement
    |WHERE $primaryKeyField = ?
    |""".stripMargin
  }
}
