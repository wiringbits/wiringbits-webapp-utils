package net.wiringbits.webapp.utils.ui.web

import io.github.nafg.simplefacade.Factory
import japgolly.scalajs.react.vdom.VdomNode
import net.wiringbits.webapp.utils.api.models.AdminGetTables
import net.wiringbits.webapp.utils.ui.web.components.{EditGuesser, ListGuesser}
import net.wiringbits.webapp.utils.ui.web.facades.reactadmin._
import net.wiringbits.webapp.utils.ui.web.models.DataExplorerSettings
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.global

import scala.concurrent.Future

object AdminView {
  private def AdminTables(api: API, response: AdminGetTables.Response, dataExplorerSettings: DataExplorerSettings) = {
    val tablesUrl = s"${api.url}/admin/tables"

    def buildResources: List[VdomNode] = {
      response.data.map { table =>
        Resource(
          _.name := table.name,
          _.list := ListGuesser(table),
          _.edit := EditGuesser(table, dataExplorerSettings)
        )
      }
    }

    val resources = buildResources
    Admin(_.dataProvider := simpleRestProvider(tablesUrl))(resources: _*)
  }

  def component(
      api: API,
      dataExplorerSettings: DataExplorerSettings = DataExplorerSettings()
  ): Future[Factory[Admin.Props]] = {
    api.client.getTables.map { AdminTables(api, _, dataExplorerSettings) }
  }
}
