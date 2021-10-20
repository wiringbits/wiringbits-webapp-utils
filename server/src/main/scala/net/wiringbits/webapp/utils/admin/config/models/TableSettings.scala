package net.wiringbits.webapp.utils.admin.config.models

import net.wiringbits.webapp.utils.admin.utils.models.ordering.OrderingCondition

case class TableSettings(tableName: String, defaultOrderByClause: OrderingCondition, IDFieldName: String)
