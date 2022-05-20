package net.wiringbits.webapp.utils.admin.repositories.daos

import anorm.{SqlParser, SqlStringInterpolation}
import net.wiringbits.webapp.utils.admin.repositories.models.{
  Cell,
  DatabaseTable,
  ForeignReference,
  TableField,
  TableRow
}
import net.wiringbits.webapp.utils.admin.utils.QueryBuilder
import net.wiringbits.webapp.utils.admin.utils.models.QueryParameters

import java.sql.Connection
import java.util.UUID
import scala.collection.mutable.ListBuffer
import scala.util.Try

object DatabaseTablesDAO {

  def all(schema: String = "public")(implicit conn: Connection): List[DatabaseTable] = {
    SQL"""
    SELECT table_name
    FROM information_schema.tables
    WHERE table_schema = $schema
      AND table_type = 'BASE TABLE'
    ORDER BY table_name
    """.as(tableParser.*)
  }

  def getTableFields(
      tableName: String
  )(implicit conn: Connection): List[TableField] = {
    val sql = s"SELECT * FROM $tableName LIMIT 0"
    val preparedStatement = conn.prepareStatement(sql)

    try {
      val resultSet = preparedStatement.executeQuery()
      val metadata = resultSet.getMetaData
      val numberOfColumns = metadata.getColumnCount
      try {
        val fields = for {
          columnNumber <- 1 to numberOfColumns
          columnName = metadata.getColumnName(columnNumber)
          columnType = metadata.getColumnTypeName(columnNumber)
        } yield TableField(columnName, columnType)
        fields.toList
      } finally {
        resultSet.close()
      }
    } finally {
      preparedStatement.close()
    }
  }

  def getTableReferences(
      tableName: String
  )(implicit conn: Connection): List[ForeignReference] = {
    // TODO: revisit query
    SQL"""
    SELECT kcu.table_schema || '.' || kcu.table_name AS foreign_table, 
      rel_tco.table_schema || '.' || rel_tco.table_name AS primary_table, 
      string_agg(kcu.column_name, ', ') AS fk_columns
    FROM information_schema.table_constraints tco
    JOIN information_schema.key_column_usage kcu
      ON tco.constraint_schema = kcu.constraint_schema
      AND tco.constraint_name = kcu.constraint_name
    JOIN information_schema.referential_constraints rco
      ON tco.constraint_schema = rco.constraint_schema
      AND tco.constraint_name = rco.constraint_name
    JOIN information_schema.table_constraints rel_tco
      ON rco.unique_constraint_schema = rel_tco.constraint_schema
      AND rco.unique_constraint_name = rel_tco.constraint_name
    WHERE tco.constraint_type = 'FOREIGN KEY'
      AND kcu.table_name = $tableName
    GROUP BY kcu.table_schema, kcu.table_name, rel_tco.table_name, rel_tco.table_schema
    ORDER BY kcu.table_schema, kcu.table_name
    """.as(tableReferencesParser.*)
  }

  def getTableData(
      tableName: String,
      fields: List[TableField],
      queryParameters: QueryParameters
  )(implicit conn: Connection): List[TableRow] = {
    val tableData = new ListBuffer[TableRow]()
    val sql =
      s"""
      SELECT * FROM $tableName
      WHERE ? = ?
      ORDER BY ${queryParameters.sort.field} ${queryParameters.sort.ordering}
      LIMIT ? OFFSET ?
      """
    val preparedStatement = conn.prepareStatement(sql)

    val limit = queryParameters.pagination.end - queryParameters.pagination.start
    val offset = queryParameters.pagination.start

    preparedStatement.setString(1, queryParameters.filter.field)
    preparedStatement.setString(2, queryParameters.filter.value)
    preparedStatement.setInt(3, limit)
    preparedStatement.setInt(4, offset)
    val resultSet = preparedStatement.executeQuery()

    try {
      while (resultSet.next) {
        val rowData = for {
          field <- fields
          fieldName = field.name
          data = resultSet.getString(fieldName)
        } yield Cell(Option(data).getOrElse("null"))
        tableData += TableRow(rowData)
      }
      tableData.toList
    } finally {
      resultSet.close()
      preparedStatement.close()
    }
  }

  def getMandatoryFields(tableName: String, primaryKeyField: String)(implicit
      conn: Connection
  ): List[TableField] = {
    val mandatoryFields = new ListBuffer[TableField]()

    val SQL =
      s"""
      SELECT column_name, is_nullable, 
        column_default, data_type
      FROM information_schema.columns
      WHERE table_schema = 'public'
        AND table_name = ?
      ORDER BY column_name
      """

    val preparedStatement = conn.prepareStatement(SQL)
    preparedStatement.setObject(1, tableName)

    val resultSet = preparedStatement.executeQuery()

    while (resultSet.next()) {
      val columnName = resultSet.getString("column_name")
      val columnType = resultSet.getString("data_type")
      val defaultValue = Option(resultSet.getString("column_default"))
      val isNullable = resultSet.getString("is_nullable") == "YES"
      val isObligatory = !isNullable && defaultValue.isEmpty
      if (isObligatory && (columnName != primaryKeyField)) {
        mandatoryFields += TableField(columnName, columnType)
      }
    }
    mandatoryFields.toList
  }

  def find(tableName: String, primaryKeyField: String, primaryKeyValue: String)(implicit
      conn: Connection
  ): Option[TableRow] = {
    val sql = s"""
    SELECT *
      FROM $tableName
    WHERE $primaryKeyField = ?
    """
    val preparedStatement = conn.prepareStatement(sql)

    preparedStatement.setObject(1, UUID.fromString(primaryKeyValue))
    val resultSet = preparedStatement.executeQuery()
    Try {
      resultSet.next()
      val numberOfColumns = resultSet.getMetaData.getColumnCount
      val row = for {
        columnNumber <- 1 to numberOfColumns
        cellData = resultSet.getString(columnNumber)

      } yield Cell(Option(cellData).getOrElse("null"))
      TableRow(row.toList)
    }.toOption
  }

  def create(tableName: String, body: Map[String, String], primaryKeyField: String)(implicit
      conn: Connection
  ): Unit = {
    val sql = QueryBuilder.create(tableName, body, primaryKeyField)
    val preparedStatement = conn.prepareStatement(sql)

    preparedStatement.setObject(1, UUID.randomUUID())
    for (i <- 2 to body.size + 1) {
      val value = body(body.keys.toList(i - 2))
      preparedStatement.setObject(i, value)
    }
    val _ = preparedStatement.executeUpdate()
  }

  def update(
      tableName: String,
      fieldsAndValues: Map[TableField, String],
      primaryKeyField: String,
      primaryKeyValue: String
  )(implicit conn: Connection): Unit = {
    val sql = QueryBuilder.update(tableName, fieldsAndValues, primaryKeyField)
    val preparedStatement = conn.prepareStatement(sql)

    val notNullData = fieldsAndValues.filterNot(_._2 == "null")
    notNullData.zipWithIndex.foreach { case ((_, value), i) =>
      preparedStatement.setObject(i + 1, value)
    }
    // where ... = ?
    preparedStatement.setObject(notNullData.size + 1, UUID.fromString(primaryKeyValue))
    val _ = preparedStatement.executeUpdate()
  }

  def delete(tableName: String, primaryKeyField: String, primaryKeyValue: String)(implicit
      conn: Connection
  ): Unit = {
    val sql = s"""
      DELETE FROM $tableName
      WHERE $primaryKeyField = ?
      """
    val preparedStatement = conn.prepareStatement(sql)

    preparedStatement.setObject(1, UUID.fromString(primaryKeyValue))
    val _ = preparedStatement.executeUpdate()
  }

  def countRecordsOnTable(tableName: String)(implicit conn: Connection): Int = {
    SQL"""
      SELECT COUNT(*)
      FROM #$tableName
      """.as(SqlParser.int("count").single)
  }
}
