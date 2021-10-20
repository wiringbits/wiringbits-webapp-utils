package net.wiringbits.webapp.utils.admin.repositories.models

case class TableMetadata(name: String, fields: List[TableField], rows: List[TableRow])
