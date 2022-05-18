package net.wiringbits.webapp.utils.ui.web

import japgolly.scalajs.react.vdom.VdomNode
import net.wiringbits.webapp.utils.api.models.AdminGetTables
import net.wiringbits.webapp.utils.ui.web.facades.reactadmin.{Admin, ReactAdmin, Resource, simpleRestProvider}
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.global

object AdminView {
  private def AdminTables(api: API, response: AdminGetTables.Response) = {
    val tablesUrl = s"${api.url}/admin/tables"
    val tableNames = response.data.map(_.name)

    def buildResources: List[VdomNode] = {
      tableNames.map { tableName =>
        Resource(
          _.name := tableName,
          _.list := ReactAdmin.ListGuesser,
          _.edit := ReactAdmin.EditGuesser
        )
      }
    }

    val resources = buildResources
    Admin(
      _.dataProvider :=
        simpleRestProvider(tablesUrl)
    )(resources: _*)
  }

  def component(api: API) = {
    api.client.getTables.map { AdminTables(api, _) }
  }
}
