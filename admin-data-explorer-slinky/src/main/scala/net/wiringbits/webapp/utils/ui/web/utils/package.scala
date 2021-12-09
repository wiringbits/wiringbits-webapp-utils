package net.wiringbits.webapp.utils.ui.web

package object utils {

  private def upperCaseFirstLetter(word: String): String = {
    word.headOption.map(_.toUpper).map(_ + word.tail).getOrElse("")
  }

  def snakeCaseToUpper(word: String): String = {
    word
      .split("_")
      .map(upperCaseFirstLetter)
      .mkString(" ")
  }
}
