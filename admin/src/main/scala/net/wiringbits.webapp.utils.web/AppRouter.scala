package net.wiringbits.webapp.utils.web

import net.wiringbits.webapp.utils.ui.components.core.widgets.Scaffold
import net.wiringbits.webapp.utils.web.components.pages.{DataExplorerPage, ExperimentalTablesPage, HomePage}
import net.wiringbits.webapp.utils.web.components.widgets.{AppBar, Footer}
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import typings.reactRouter.mod.RouteProps
import typings.reactRouterDom.components.Route
import typings.reactRouterDom.{components => router}

@react object AppRouter {
  case class Props(api: API)

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    def generateRoute(path: String, child: => ReactElement): Route.Builder[RouteProps] = {
      router.Route(
        RouteProps()
          .setExact(true)
          .setPath(path)
          .setRender { route =>
            Scaffold(appbar = Some(AppBar()), body = child, footer = Some(Footer()))
          }
      )
    }

    val home = generateRoute("/", HomePage())
    val dataExplorerPage = generateRoute("/tables", DataExplorerPage(props.api))
    val tablePage = generateRoute("/tables/:tableName", ExperimentalTablesPage(props.api))
    val catchAllRoute = router.Route(
      RouteProps().setRender { _ =>
        router.Redirect("/")
      }
    )

    router.Switch(home, dataExplorerPage, tablePage, catchAllRoute)
  }
}
