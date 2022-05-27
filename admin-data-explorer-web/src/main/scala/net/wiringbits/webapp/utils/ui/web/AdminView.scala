package net.wiringbits.webapp.utils.ui.web

import io.github.nafg.simplefacade.Factory
import japgolly.scalajs.react.vdom.VdomNode
import net.wiringbits.webapp.utils.api.models.AdminGetTables
import net.wiringbits.webapp.utils.ui.web.components.{EditGuesser, ListGuesser}
import net.wiringbits.webapp.utils.ui.web.facades.reactadmin._
import net.wiringbits.webapp.utils.ui.web.models.TableAction
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.global

import scala.concurrent.Future

object AdminView {
  private def AdminTables(api: API, response: AdminGetTables.Response, tableActions: List[TableAction]) = {
    val tablesUrl = s"${api.url}/admin/tables"

    def buildResources: List[VdomNode] = {
      response.data.map { table =>
        val actions = tableActions.find(_.tableName == table.name)
        Resource(
          _.name := table.name,
          _.list := ListGuesser(table),
          _.edit := EditGuesser(table, actions)
        )
      }
    }

    val resources = buildResources
    Admin(_.dataProvider := simpleRestProvider(tablesUrl))(resources: _*)
  }

  def component(api: API, tableActions: List[TableAction] = List.empty): Future[Factory[Admin.Props]] = {
    api.client.getTables.map { AdminTables(api, _, tableActions) }
  }
}
