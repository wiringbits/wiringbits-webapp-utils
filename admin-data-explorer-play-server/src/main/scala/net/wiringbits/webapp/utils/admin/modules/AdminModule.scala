package net.wiringbits.webapp.utils.admin.modules

import net.wiringbits.webapp.utils.admin.tasks.DataExplorerConfigValidatorTask
import play.api.inject
import play.api.inject.SimpleModule

class AdminModule extends SimpleModule(inject.bind[DataExplorerConfigValidatorTask].toSelf.eagerly())
