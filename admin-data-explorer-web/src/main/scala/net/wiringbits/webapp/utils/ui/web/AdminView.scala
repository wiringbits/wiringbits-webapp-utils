package net.wiringbits.webapp.utils.ui.web

import io.github.nafg.simplefacade.Factory
import japgolly.scalajs.react.vdom.VdomNode
import net.wiringbits.webapp.utils.api.models.AdminGetTables
import net.wiringbits.webapp.utils.api.models.AdminGetTables.Response.TableField
import net.wiringbits.webapp.utils.ui.web.components.TableFieldsList
import net.wiringbits.webapp.utils.ui.web.facades.reactadmin._
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.global

import scala.concurrent.Future

object AdminView {
  private def AdminTables(api: API, response: AdminGetTables.Response) = {
    val tablesUrl = s"${api.url}/admin/tables"

    def buildResources: List[VdomNode] = {
      response.data.map { table =>
        Resource(
          _.name := table.name,
          _.list := TableFieldsList.component(table),
          _.edit := createEditFromTableFields(table.fields)
        )
      }
    }

    def createEditFromTableFields(tableFields: List[TableField]) = {
      println(tableFields)
      ReactAdmin.EditGuesser
    }

    val resources = buildResources
    Admin(
      _.dataProvider :=
        simpleRestProvider(tablesUrl)
    )(resources: _*)
  }

  def component(api: API): Future[Factory[Admin.Props]] = {
    api.client.getTables.map { AdminTables(api, _) }
  }
}
