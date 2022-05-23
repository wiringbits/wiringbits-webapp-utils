package net.wiringbits.webapp.utils.ui.web.utils

import net.wiringbits.webapp.utils.api.models.AdminGetTables
import net.wiringbits.webapp.utils.ui.web.models.{Field, FieldType}

object ResponseGuesser {
  def getTypesFromResponse(response: AdminGetTables.Response.DatabaseTable): List[Field] = {
    response.fields.map { field =>
      val fieldType = FieldType.fromTableField(field)
      Field(name = field.name, `type` = fieldType, disabled = field.disabled)
    }
  }
}
