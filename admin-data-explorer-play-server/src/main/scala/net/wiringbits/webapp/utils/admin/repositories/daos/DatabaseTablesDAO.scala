package net.wiringbits.webapp.utils.admin.repositories.daos

import anorm.{SqlParser, SqlStringInterpolation}
import net.wiringbits.webapp.utils.admin.config.DataExplorerSettings
import net.wiringbits.webapp.utils.admin.repositories.models.{Cell, DatabaseTable, TableField, TableMetadata, TableRow}
import net.wiringbits.webapp.utils.admin.utils.models.pagination.{Count, Limit, Offset, PaginatedQuery, PaginatedResult}

import java.sql.Connection
import java.util.UUID
import scala.collection.mutable.ListBuffer

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

  def getTablesInSettings(tableSettings: DataExplorerSettings): List[DatabaseTable] = {
    for {
      table <- tableSettings.tables
      tableName = table.tableName
    } yield DatabaseTable(tableName)
  }

  def getTableFields(
      tableName: String
  )(implicit conn: Connection): List[TableField] = {
    val sql = f"SELECT * FROM $tableName LIMIT 0"

    val preparedStatement = conn.prepareStatement(sql)
    try {
      val resultSet = preparedStatement.executeQuery()
      try {
        val metadata = resultSet.getMetaData
        val numberOfColumns = metadata.getColumnCount
        val columnNullable = 1
        val fields = for {
          columnNumber <- 1 to numberOfColumns
          columnName = metadata.getColumnName(columnNumber)
          columnType = metadata.getColumnTypeName(columnNumber)
          isNullable = metadata.isNullable(columnNumber) == columnNullable
        } yield TableField(columnName, columnType, isNullable)
        fields.toList
      } finally {
        resultSet.close()
      }
    } finally {
      preparedStatement.close()
    }
  }

  def getTableData(
      tableName: String,
      fields: List[TableField],
      pagination: PaginatedQuery,
      tableSettings: DataExplorerSettings
  )(implicit conn: Connection): PaginatedResult[TableMetadata] = {
    val tableData = new ListBuffer[TableRow]()

    val indexOfTable = tableSettings.tables.indexWhere(_.tableName == tableName)
    val defaultOrderBy = tableSettings.tables(indexOfTable).defaultOrderByClause
    val numberOfRecords = countRecordsOnTable(tableName)

    val SQL =
      s"""
        SELECT * FROM $tableName
        ORDER BY ?
        LIMIT ? OFFSET ?
        """

    val preparedStatement = conn.prepareStatement(SQL)
    preparedStatement.setString(1, defaultOrderBy.string)
    preparedStatement.setInt(2, pagination.limit.int)
    preparedStatement.setInt(3, pagination.offset.int)

    val resultSet = preparedStatement.executeQuery()

    try {
      while (resultSet.next) {
        val rowData = for {
          field <- fields
          fieldName = field.name
          data = resultSet.getString(fieldName)
          // This is just a workaround. I think it'll be better if I use a Option[T] syntax
          // so I'll do it later
        } yield Cell(Option(data).getOrElse("null"))

        tableData += TableRow(rowData)
      }
      val table = TableMetadata(tableName, fields, tableData.toList)
      PaginatedResult[TableMetadata](
        table,
        Offset(pagination.offset.int),
        Limit(pagination.limit.int),
        Count(numberOfRecords)
      )
    } finally {
      resultSet.close()
      preparedStatement.close()
    }
  }

  def getMandatoryFields(tableName: String, tableSettings: DataExplorerSettings)(implicit
      conn: Connection
  ): List[TableField] = {
    val mandatoryFields = new ListBuffer[TableField]()

    // TODO: Avoid Option#get
    val idFieldName = tableSettings.tables.find(_.tableName == tableName).get.idFieldName
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
      if (isObligatory && (columnName != idFieldName)) {
        mandatoryFields += TableField(columnName, columnType, isNullable)
      }
    }
    mandatoryFields.toList
  }

  def find(tableName: String, ID: String, tableSettings: DataExplorerSettings)(implicit
      conn: Connection
  ): (TableRow, List[TableField]) = {
    // TODO: Avoid Option#get
    val fields = getTableFields(tableName)
    val idFieldName = tableSettings.tables.find(_.tableName == tableName).get.idFieldName
    val SQL =
      s"""
      SELECT *
      FROM $tableName
      WHERE $idFieldName = ?
      """

    val preparedStatement = conn.prepareStatement(SQL)
    preparedStatement.setObject(1, UUID.fromString(ID))

    val resultSet = preparedStatement.executeQuery()

    resultSet.next()

    val numberOfColumns = resultSet.getMetaData.getColumnCount
    val row = for {
      columnNumber <- 1 to numberOfColumns
      cellData = resultSet.getString(columnNumber)
    } yield Cell(Option(cellData).getOrElse("null"))
    (TableRow(row.toList), fields)
  }

  def create(tableName: String, body: Map[String, String], tableSettings: DataExplorerSettings)(implicit
      conn: Connection
  ): Unit = {
    val idFieldName = tableSettings.tables.find(_.tableName == tableName).get.idFieldName

    val sqlFields = new StringBuilder(idFieldName)
    val sqlValues = new StringBuilder("?")
    for ((key, _) <- body) {
      sqlFields.append(s", $key")
      sqlValues.append(s", ?")
    }

    val SQL =
      s"""
      INSERT INTO $tableName
        ($sqlFields)
      VALUES (
        ${sqlValues.toString()}
      )
      """

    val preparedStatement = conn.prepareStatement(SQL)

    preparedStatement.setObject(1, UUID.randomUUID())
    for (i <- 2 to body.size + 1) {
      val value = body(body.keys.toList(i - 2))
      preparedStatement.setObject(i, value)
    }
    val _ = preparedStatement.executeUpdate()
  }

  def update(
      tableName: String,
      id: String,
      tableSettings: DataExplorerSettings,
      body: Map[String, String]
  )(implicit conn: Connection): Unit = {
    // TODO: Avoid Option#get
    val idFieldName = tableSettings.tables.find(_.tableName == tableName).get.idFieldName

    val updateStatement = new StringBuilder("SET")
    for ((name, _) <- body) {
      updateStatement.append(s" $name = ?,")
    }
    updateStatement.deleteCharAt(updateStatement.length - 1)

    val SQL = s"""
      UPDATE $tableName
      $updateStatement
      WHERE $idFieldName = ?
      """
    val preparedStatement = conn.prepareStatement(SQL)

    for (i <- 1 to body.size) {
      val value = body(body.keys.toList(i - 1))
      preparedStatement.setObject(i, value)
    }
    preparedStatement.setObject(body.size + 1, UUID.fromString(id))

    val _ = preparedStatement.executeUpdate()
  }

  def delete(tableName: String, id: String, tableSettings: DataExplorerSettings)(implicit
      conn: Connection
  ): Unit = {
    // TODO: Avoid Option#get
    val idFieldName = tableSettings.tables.find(_.tableName == tableName).get.idFieldName

    val sql =
      s"""
      DELETE FROM $tableName
      WHERE $idFieldName = ?
      """

    val preparedStatement = conn.prepareStatement(sql)
    preparedStatement.setObject(1, UUID.fromString(id))

    val _ = preparedStatement.executeUpdate()
  }

  def countRecordsOnTable(tableName: String)(implicit conn: Connection): Int = {
    SQL"""
      SELECT COUNT(*)
      FROM #$tableName
       """.as(SqlParser.int("count").single)
  }
}
