package net.wiringbits.webapp.utils.common.models.core

trait WrappedInt extends Any {
  def int: Int

  override def toString: String = int.toString

  override def equals(obj: Any): Boolean = obj match {
    case that: WrappedInt => that.int == int
    case _ => false
  }

  override def hashCode(): Int = int.hashCode
}
