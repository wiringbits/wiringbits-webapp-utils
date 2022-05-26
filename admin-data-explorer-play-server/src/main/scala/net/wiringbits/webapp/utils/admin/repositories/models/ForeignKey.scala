package net.wiringbits.webapp.utils.admin.repositories.models

case class ForeignKey(foreignTable: String, primaryTable: String, foreignColumnName: String)
