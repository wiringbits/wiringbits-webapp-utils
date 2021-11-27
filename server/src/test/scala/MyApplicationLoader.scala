import net.wiringbits.webapp.utils.admin.controllers.AdminController
import play.api.*
import play.api.ApplicationLoader.Context
import play.api.routing.Router
import play.api.routing.sird.*
import play.filters.HttpFiltersComponents

import javax.inject.Inject

class MyApplicationLoader @Inject() (adminController: AdminController) extends ApplicationLoader {
  def load(context: Context) = new MyComponents(adminController, context).application
}

class MyComponents @Inject() (adminController: AdminController, context: Context)
    extends BuiltInComponentsFromContext(context)
    with HttpFiltersComponents {

  override def router: Router = Router.from(
    {
      case GET(p"/admin/tables") =>
        adminController.getTables()

      case GET(p"/admin/tables/$tableName" ? q_o"offset=${int(offsetOpt)}" & q_o"limit=${int(limitOpt)}") =>
        val offset = offsetOpt.getOrElse(0)
        val limit = limitOpt.getOrElse(10)
        adminController.getTableMetadata(tableName, offset, limit)
    }
  )
}

/*
class AppRouter @Inject() (adminController: AdminController) extends SimpleRouter {

  override def routes: Routes = {
    case GET(p"/admin/tables") =>
      adminController.getTables()

    case GET(p"/admin/tables/$tableName" ? q_o"offset=${int(offsetOpt)}" & q_o"limit=${int(limitOpt)}") =>
      val offset = offsetOpt.getOrElse(0)
      val limit = limitOpt.getOrElse(10)
      adminController.getTableMetadata(tableName, offset, limit)
  }
}

@Singleton
class AppRoutesProvider @Inject() (appRouter: AppRouter, httpConfig: HttpConfiguration) extends Provider[Router] {
  lazy val get = appRouter.withPrefix(httpConfig.context)
}

class ScalaGuiceAppLoader extends GuiceApplicationLoader {

  override def builder(context: ApplicationLoader.Context): GuiceApplicationBuilder = {
    initialBuilder
      .in(context.environment)
      .loadConfig(context.initialConfiguration)
      .overrides(overrides(context) *)
  }

  protected override def overrides(context: ApplicationLoader.Context): Seq[GuiceableModule] =
    super.overrides(context) :+ (api.inject.bind[Router].toProvider[AppRoutesProvider]: GuiceableModule)
}
 */
