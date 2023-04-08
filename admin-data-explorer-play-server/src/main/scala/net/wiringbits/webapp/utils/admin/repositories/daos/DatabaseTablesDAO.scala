package net.wiringbits.webapp.utils.admin.repositories.daos

import anorm.{SqlParser, SqlStringInterpolation}
import net.wiringbits.webapp.utils.admin.config.{CustomDataType, PrimaryKeyDataType, TableSettings}
import net.wiringbits.webapp.utils.admin.repositories.models.*
import net.wiringbits.webapp.utils.admin.utils.models.{FilterParameter, QueryParameters}
import net.wiringbits.webapp.utils.admin.utils.{QueryBuilder, StringRegex}

import java.sql.{Connection, Date, PreparedStatement, ResultSet}
import java.time.LocalDate
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

  def getTableColumns(
      tableName: String
  )(implicit conn: Connection): List[TableColumn] = {
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
        } yield TableColumn(columnName, columnType)
        fields.toList
      } finally {
        resultSet.close()
      }
    } finally {
      preparedStatement.close()
    }
  }

  def getForeignKeys(
      tableName: String
  )(implicit conn: Connection): List[ForeignKey] = {
    SQL"""
    SELECT kcu.table_name AS foreign_table, 
      rel_tco.table_name AS primary_table, 
      kcu.column_name AS fk_column
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
    GROUP BY kcu.table_schema, kcu.table_name, kcu.column_name, rel_tco.table_name, rel_tco.table_schema
    ORDER BY kcu.table_schema, kcu.table_name
    """.as(foreignKeyParser.*)
  }

  def getTableData(
      settings: TableSettings,
      columns: List[TableColumn],
      queryParameters: QueryParameters,
      baseUrl: String
  )(implicit conn: Connection): List[TableRow] = {
    val dateRegex = StringRegex.dateRegex
    val limit = queryParameters.pagination.end - queryParameters.pagination.start
    val offset = queryParameters.pagination.start
    val tableName = settings.tableName
    // react-admin gives us a "id" field instead of the primary key of the actual column so we need to replace it
    val sortBy = if (queryParameters.sort.field == "id") settings.primaryKeyField else queryParameters.sort.field

    val conditionsSql = queryParameters.filters
      .map { case FilterParameter(filterField, filterValue) =>
        filterValue match {
          case dateRegex(_, _, _) =>
            s"DATE($filterField) = ?"

          case _ =>
            if (filterValue.toIntOption.isDefined || filterValue.toDoubleOption.isDefined)
              s"$filterField = ?"
            else
              s"$filterField LIKE ?"
        }
      }
      .mkString("WHERE ", " AND ", " ")

    val sql =
      s"""
      SELECT * FROM $tableName
      ${if (queryParameters.filters.nonEmpty) conditionsSql else ""}
      ORDER BY $sortBy ${queryParameters.sort.ordering}
      LIMIT $limit OFFSET $offset
      """
    val preparedStatement = conn.prepareStatement(sql)

    queryParameters.filters.zipWithIndex
      .foreach { case (FilterParameter(_, filterValue), index) =>
        // We have to increment index by 1 because SQL parameterIndex starts in 1
        val sqlIndex = index + 1

        filterValue match {
          case dateRegex(year, month, day) =>
            val parsedDate = LocalDate.of(year.toInt, month.toInt, day.toInt)
            preparedStatement.setDate(sqlIndex, Date.valueOf(parsedDate))

          case _ =>
            if (filterValue.toIntOption.isDefined)
              preparedStatement.setInt(sqlIndex, filterValue.toInt)
            else if (filterValue.toDoubleOption.isDefined)
              preparedStatement.setDouble(sqlIndex, filterValue.toDouble)
            else
              preparedStatement.setString(sqlIndex, s"%$filterValue%")
        }
      }

    val resultSet = preparedStatement.executeQuery()
    val tableData = new ListBuffer[TableRow]()
    try {
      while (resultSet.next) {
        val rowData = for {
          column <- columns
          columnName = column.name
          stringData = getStringFromColumnName(
            settings = settings,
            resultSet = resultSet,
            columnName = columnName,
            baseUrl = baseUrl
          )
        } yield Cell(stringData)
        tableData += TableRow(rowData)
      }
      tableData.toList
    } finally {
      resultSet.close()
      preparedStatement.close()
    }
  }

  private def getStringFromColumnName(
      settings: TableSettings,
      resultSet: ResultSet,
      columnName: String,
      baseUrl: String
  ) = {
    val maybe = settings.columnTypeOverrides.get(columnName)
    val data = maybe
      .map {
        case CustomDataType.BinaryImage =>
          val rowId = resultSet.getString(settings.primaryKeyField)
          s"$baseUrl/admin/images/${settings.tableName}/$columnName/$rowId"
        // TODO: handle binary file
        case CustomDataType.Binary => resultSet.getString(columnName)
      }
      .getOrElse(resultSet.getString(columnName))
    Option(data).getOrElse("")
  }

  def getMandatoryFields(tableName: String, primaryKeyField: String)(implicit conn: Connection): List[TableColumn] = {
    SQL"""
      SELECT column_name, data_type
      FROM information_schema.columns
      WHERE table_schema = 'public'
        AND is_nullable = 'NO'
        AND column_default IS NULL
        AND table_name = $tableName
        AND column_name != $primaryKeyField
      ORDER BY column_name
      """.as(tableColumnParser.*)
  }

  def find(settings: TableSettings, columns: List[TableColumn], primaryKeyValue: String, baseUrl: String)(implicit
      conn: Connection
  ): Option[TableRow] = {
    val sql = s"""
      SELECT *
      FROM ${settings.tableName}
      WHERE ${settings.primaryKeyField} = ?
      """
    val preparedStatement = conn.prepareStatement(sql)

    setPreparedStatementKey(preparedStatement, primaryKeyValue, settings.primaryKeyDataType)
    val resultSet = preparedStatement.executeQuery()
    Try {
      resultSet.next()
      val row = for {
        column <- columns
        columnName = column.name
        stringData = getStringFromColumnName(
          settings = settings,
          resultSet = resultSet,
          columnName = columnName,
          baseUrl = baseUrl
        )
      } yield Cell(stringData)
      TableRow(row.toList)
    }.toOption
  }

  private def setPreparedStatementKey(
      preparedStatement: PreparedStatement,
      primaryKeyValue: String,
      primaryKeyType: PrimaryKeyDataType,
      parameterIndex: Int = 1
  ): Unit = {
    primaryKeyType match { // regular string unless UUID
      case PrimaryKeyDataType.UUID => preparedStatement.setObject(parameterIndex, UUID.fromString(primaryKeyValue))
      // TODO: Handle errors when the value is not an integer
      case PrimaryKeyDataType.Serial =>
        preparedStatement.setInt(parameterIndex, primaryKeyValue.toInt) // use setInt instead of setObject
      case PrimaryKeyDataType.BigSerial =>
        preparedStatement.setLong(parameterIndex, primaryKeyValue.toLong) // use setLong instead of setObject
    }
  }
  def create(
      tableName: String,
      body: Map[String, String],
      primaryKeyField: String,
      primaryKeyType: PrimaryKeyDataType = PrimaryKeyDataType.UUID
  )(implicit
      conn: Connection
  ): Unit = {
    val sql = QueryBuilder.create(tableName, body, primaryKeyField, primaryKeyType)
    val preparedStatement = conn.prepareStatement(sql)

    var i = 0
    if (primaryKeyType == PrimaryKeyDataType.UUID) {
      i = i + 1
      preparedStatement.setObject(i, UUID.randomUUID())
    }
    // NOTE: QueryBuilder.create needs primaryKeyType parameter because it seems there is no way to pass DEFAULT
    // into prepared statement parameter. Must be set literally in QueryBuilder.create
    // eg. NULL can be used in MySQL to generate default value in an autoincrement column, but not Postgres unfortunately
    // Postgres: INSERT INTO test_serial (id) VALUES(DEFAULT); MySQL: INSERT INTO table (id) VALUES(NULL)

    for (j <- i + 1 to body.size + i) {
      val value = body(body.keys.toList(j - i - 1))
      preparedStatement.setObject(j, value)
    }
    val _ = preparedStatement.executeUpdate()
  }

  def update(
      tableName: String,
      fieldsAndValues: Map[TableColumn, String],
      primaryKeyField: String,
      primaryKeyValue: String,
      primaryKeyType: PrimaryKeyDataType = PrimaryKeyDataType.UUID
  )(implicit conn: Connection): Unit = {
    val sql = QueryBuilder.update(tableName, fieldsAndValues, primaryKeyField)
    val preparedStatement = conn.prepareStatement(sql)

    val notNullData = fieldsAndValues.filterNot { case (_, value) => value == "null" }
    notNullData.zipWithIndex.foreach { case ((_, value), i) =>
      preparedStatement.setObject(i + 1, value)
    }
    // where ... = ?
    setPreparedStatementKey(preparedStatement, primaryKeyValue, primaryKeyType, notNullData.size + 1)
    val _ = preparedStatement.executeUpdate()
  }

  def delete(
      tableName: String,
      primaryKeyField: String,
      primaryKeyValue: String,
      primaryKeyType: PrimaryKeyDataType = PrimaryKeyDataType.UUID
  )(implicit
      conn: Connection
  ): Unit = {
    val sql = s"""
      DELETE FROM $tableName
      WHERE $primaryKeyField = ?
      """
    val preparedStatement = conn.prepareStatement(sql)
    setPreparedStatementKey(preparedStatement, primaryKeyValue, primaryKeyType)
    val _ = preparedStatement.executeUpdate()
  }

  def countRecordsOnTable(tableName: String)(implicit conn: Connection): Int = {
    SQL"""
      SELECT COUNT(*)
      FROM #$tableName
      """.as(SqlParser.int("count").single)
  }

  def getImageData(settings: TableSettings, columnName: String, imageId: String)(implicit
      conn: Connection
  ): Option[Array[Byte]] = {
    val sql = s"""
      SELECT $columnName
      FROM ${settings.tableName}
      WHERE ${settings.primaryKeyField} = ?
      """
    val preparedStatement = conn.prepareStatement(sql)
    setPreparedStatementKey(preparedStatement, imageId, settings.primaryKeyDataType)
    val resultSet = preparedStatement.executeQuery()
    Try {
      resultSet.next()
      resultSet.getBytes(columnName)
    }.toOption
  }
}
