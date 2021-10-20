package net.wiringbits.webapp.utils.admin.modules

import net.wiringbits.webapp.utils.admin.config.AdminConfig
import play.api.inject
import play.api.inject.SimpleModule

class AdminModule extends SimpleModule(inject.bind[AdminConfig].toSelf.eagerly())
