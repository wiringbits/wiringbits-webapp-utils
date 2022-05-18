package net.wiringbits.webapp.utils.admin.modules

import com.google.inject.{AbstractModule, Provides}
import net.wiringbits.webapp.utils.admin.config.{DataExplorerSettings, TableSettings}

class DataExplorerTestModule extends AbstractModule {

  @Provides()
  def dataExplorerSettings: DataExplorerSettings = {
    DataExplorerSettings(settings)
  }

  val settings: List[TableSettings] = List(
    TableSettings("users", "user_id")
  )
}
