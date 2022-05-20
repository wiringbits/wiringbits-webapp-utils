package net.wiringbits.webapp.utils.admin.repositories.models

case class ForeignReference(foreignTable: String, primaryTable: String, columnName: String)
