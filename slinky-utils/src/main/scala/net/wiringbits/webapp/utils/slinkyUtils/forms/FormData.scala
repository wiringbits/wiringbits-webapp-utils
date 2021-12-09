package net.wiringbits.webapp.utils.slinkyUtils.forms

trait FormData[RequestModel] {
  private def requiredFieldsCompleted: Boolean = fields.filter(_.required).forall(_.isValid)
  private def optionalFieldsValid: Boolean = fields.filterNot(_.required).forall(_.isValid)

  // the list of typed fields available for the form
  def fields: List[FormField[_]]

  def fieldsError: Option[String] = {
    if (!requiredFieldsCompleted) {
      Some("The required fields need to be filled")
    } else if (!optionalFieldsValid) {
      Some("The optional fields need to be either absent or be valid")
    } else {
      None
    }
  }

  // custom validation errors, usually, complex logic that involves many fields from the form
  def formValidationErrors: List[String] = fieldsError.toList ::: fields.flatMap(_.errorMsg)

  // true when the form is in a valid state
  def isValid: Boolean = {
    requiredFieldsCompleted &&
    optionalFieldsValid &&
    formValidationErrors.isEmpty
  }

  def submitRequest: Option[RequestModel]
}
