package net.wiringbits.webapp.utils.admin.config

import net.wiringbits.webapp.utils.admin.utils.models.ordering.OrderingCondition

case class TableSettings(tableName: String, defaultOrderByClause: OrderingCondition, idFieldName: String)
