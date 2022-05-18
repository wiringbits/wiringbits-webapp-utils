package net.wiringbits.webapp.utils.ui.web.models

sealed trait FieldDataType extends Product with Serializable

object FieldDataType {
  case object Date extends FieldDataType
  case object Text extends FieldDataType
  case object Email extends FieldDataType
  case class Reference(reference: String) extends FieldDataType
}
