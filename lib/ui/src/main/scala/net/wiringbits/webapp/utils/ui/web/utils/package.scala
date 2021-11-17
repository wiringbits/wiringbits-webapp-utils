package net.wiringbits.webapp.utils.ui.web

package object utils {

  private def upperCaseFirstLetter(word: String): String = {
    // This replaces every ocurrence
    word.replace(word.head, word.head.toUpper)
  }

  def formatField(word: String): String = {
    val splittedArray = word.split("_")
    splittedArray.map(upperCaseFirstLetter).mkString(" ")
  }
}