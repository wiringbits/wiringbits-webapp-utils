package net.wiringbits.webapp.utils.ui.web

package object utils {

  private def upperCaseFirstLetter(word: String): String = {
    // TODO: This likely does the same but it is safer
    //     word.headOption
    //      .map(_.toUpper)
    //      .map(_ + word.tail)
    //      .getOrElse("")
    // This replaces every ocurrence
    word.replace(word.head, word.head.toUpper)
  }

  // TODO: This converts "snake_case" to "Upper Case" words, let's get a proper name for it
  def formatField(word: String): String = {
    word
      .split("_")
      .map(upperCaseFirstLetter)
      .mkString(" ")
  }
}
