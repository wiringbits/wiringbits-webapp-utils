package net.wiringbits.webapp.utils.common.models.core

trait WrappedString extends Any {
  def string: String

  override def toString: String = string

  override def equals(obj: Any): Boolean = obj match {
    case that: WrappedString => that.string == string
    case _ => false
  }

  override def hashCode(): Int = string.hashCode
}
