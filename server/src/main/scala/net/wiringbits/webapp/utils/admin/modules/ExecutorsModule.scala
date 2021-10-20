package net.wiringbits.webapp.utils.admin.modules

import com.google.inject.AbstractModule
import net.wiringbits.webapp.utils.admin.executors.DatabaseExecutionContext

class ExecutorsModule extends AbstractModule {

  override def configure(): Unit = {
    val _ = bind(classOf[DatabaseExecutionContext]).to(classOf[DatabaseExecutionContext.AkkaBased]).asEagerSingleton()
  }
}
