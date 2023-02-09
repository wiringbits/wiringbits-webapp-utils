package net.wiringbits.webapp.utils.ui.web.facades.reactadmin

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport("react-admin", JSImport.Namespace)
@js.native
object ReactAdmin extends js.Object {
  def useEditContext(): js.Dictionary[js.Any] = js.native

  val Admin, Resource, EditGuesser, ListGuesser, TextInput, ImageField, NumberInput, DateTimeInput, ReferenceInput,
      SelectInput, Button, DeleteButton, SaveButton, TopToolbar, Toolbar, Edit, SimpleForm, DateField, TextField,
      EmailField, NumberField, ReferenceField, DateInput, FilterButton, ExportButton, ComponentList, Datagrid: js.Object =
    js.native
}
