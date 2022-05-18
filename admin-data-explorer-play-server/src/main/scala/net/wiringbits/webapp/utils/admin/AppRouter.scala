package net.wiringbits.webapp.utils.admin

import net.wiringbits.webapp.utils.admin.controllers.AdminController
import net.wiringbits.webapp.utils.admin.utils.StringToDataTypesExt
import net.wiringbits.webapp.utils.admin.utils.models.{
  FilterParameter,
  PaginationParameter,
  QueryParameters,
  SortParameter
}
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird.*

import javax.inject.Inject

class AppRouter @Inject() (adminController: AdminController) extends SimpleRouter {

  override def routes: Routes = {
    // get database tables
    case GET(p"/admin/tables") =>
      adminController.getTables()

    // get database table fields
    // example: GET http://localhost:9000/admin/tables/users?filter={}&range=[0, 9]&sort=["id", "ASC"]
    case GET(p"/admin/tables/$tableName" ? q"filter=$fieldStr" & q"range=$rangeStr" & q"sort=$sortStr") =>
      val sortParameter = SortParameter.fromString(sortStr)
      val paginationParameter = PaginationParameter.fromString(rangeStr)
      val filterParameter = FilterParameter.fromString(fieldStr)

      val queryParameters =
        QueryParameters(sort = sortParameter, pagination = paginationParameter, filter = filterParameter)
      adminController.getTableMetadata(tableName, queryParameters)

    // get table resource by id (depends on IDFieldName on AdminConfig)
    case GET(p"/admin/tables/$tableName/$primaryKeyValue") =>
      adminController.find(tableName, primaryKeyValue)

    // get table resources by ids
    case GET(p"/admin/tables/$tableName" ? q"filter=$fieldStr") =>
      // fieldStr is a string like: "List(..., ..., ...)" that's why we substring it
      val filter = fieldStr.substring(6, fieldStr.length - 1).toStringList
      adminController.find(tableName, filter)

    // create table resource
    case POST(p"/admin/tables/$tableName") =>
      adminController.create(tableName)

    // update table resource
    case PUT(p"/admin/tables/$tableName/$primaryKeyValue") =>
      adminController.update(tableName, primaryKeyValue)

    // delete table resource
    case DELETE(p"/admin/tables/$tableName/$id") =>
      adminController.delete(tableName, id)
  }
}
