package net.wiringbits.webapp.utils.ui.web.facades.reactadmin

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import japgolly.scalajs.react.facade.React.ElementType

@js.native
@JSImport("react-admin", JSImport.Namespace)
object ReactAdmin extends js.Any {
  def useRecordContext(): js.Dictionary[js.Any] = js.native

  val EditGuesser, ListGuesser: ElementType = js.native

  val Admin, Create, Datagrid, Edit, EditButton, EmailField, List, ReferenceField, ReferenceInput, Resource,
      SelectInput, SimpleForm, TextField, DateField, TextInput, UrlField, fetchUtils: js.Object =
    js.native
}
