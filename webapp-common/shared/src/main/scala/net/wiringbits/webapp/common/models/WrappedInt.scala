package net.wiringbits.webapp.common.models

trait WrappedInt {
  def int: Int

  override def toString: String = int.toString

  override def equals(obj: Any): Boolean = obj match {
    case that: WrappedInt => that.int == int
    case _ => false
  }

  override def hashCode(): Int = int.hashCode
}
