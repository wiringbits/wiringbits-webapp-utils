package net.wiringbits.webapp.utils.slinkyUtils.forms

case class FakeFormData(
    override val formValidationErrors: List[String] = List.empty,
    fields: List[FormField[_]] = List.empty
) extends FormData[String] {

  override def submitRequest: Option[String] = None
}
