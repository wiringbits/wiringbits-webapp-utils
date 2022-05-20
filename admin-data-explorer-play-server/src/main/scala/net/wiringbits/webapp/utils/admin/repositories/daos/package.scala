package net.wiringbits.webapp.utils.admin.repositories

import anorm.*
import net.wiringbits.webapp.utils.admin.repositories.models.{DatabaseTable, ForeignReference}

package object daos {
  import anorm.{Column, MetaDataItem, TypeDoesNotMatch}
  import org.postgresql.util.PGobject

  implicit val citextToString: Column[String] = Column.nonNull { case (value, meta) =>
    val MetaDataItem(qualified, _, clazz) = meta
    value match {
      case str: String => Right(str)
      case obj: PGobject if "citext" equalsIgnoreCase obj.getType => Right(obj.getValue)
      case _ =>
        Left(
          TypeDoesNotMatch(
            s"Cannot convert $value: ${value.asInstanceOf[AnyRef].getClass} to String for column $qualified, class = $clazz"
          )
        )
    }
  }

  val tableParser: RowParser[DatabaseTable] = {
    Macro.parser[DatabaseTable](
      "table_name"
    )
  }

  val tableReferencesParser: RowParser[ForeignReference] = {
    Macro.parser[ForeignReference](
      "foreign_table",
      "primary_table",
      "fk_columns"
    )
  }

}
