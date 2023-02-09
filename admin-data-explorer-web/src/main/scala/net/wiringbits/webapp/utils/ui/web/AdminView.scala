package net.wiringbits.webapp.utils.ui.web

import net.wiringbits.webapp.utils.api.models.AdminGetTables
import net.wiringbits.webapp.utils.ui.web.components.{EditGuesser, ListGuesser}
import net.wiringbits.webapp.utils.ui.web.facades.reactadmin._
import net.wiringbits.webapp.utils.ui.web.facades.simpleRestProvider
import net.wiringbits.webapp.utils.ui.web.models.DataExplorerSettings
import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.global
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.{Hooks, ReactElement}

import scala.util.{Failure, Success}

@react
object AdminView {
  case class Props(api: API, dataExplorerSettings: DataExplorerSettings = DataExplorerSettings())

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val (tables, setTables) = Hooks.useState[List[AdminGetTables.Response.DatabaseTable]](List.empty)

    Hooks.useEffect(
      () => {
        props.api.client.getTables.onComplete {
          case Success(response) =>
            setTables(response.data)

          case Failure(ex) =>
            ex.printStackTrace()
        }
      },
      ""
    )

    val tablesUrl = s"${props.api.url}/admin/tables"

    def buildResources: List[ReactElement] = {
      tables.map { table =>
        Resource(
          name = table.name,
          list = ListGuesser(table),
          edit = EditGuesser(table, props.dataExplorerSettings)
        )
      }
    }

    Admin(dataProvider = simpleRestProvider(tablesUrl))(buildResources: _*)
  }
}
