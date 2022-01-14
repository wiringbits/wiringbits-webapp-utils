package net.wiringbits.webapp.utils.ui.web

package object utils {

  private def upperCaseFirstLetter(word: String): String = {
    word.headOption.map(_.toUpper).map(_.toString + word.tail).getOrElse("")
  }

  def snakeCaseToUpper(word: String): String = {
    word
      .split("_")
      .map(upperCaseFirstLetter)
      .mkString(" ")
  }

  def getChangedValues(
      fieldNames: List[String],
      initialValues: List[String],
      values: List[String]
  ): Map[String, String] = {
    val initialFieldAndValues = fieldNames.zip(initialValues).toMap
    val fieldAndvalues = fieldNames.zip(values).toMap
    fieldAndvalues.filter { x =>
      val field = x._1
      val value = x._2
      initialFieldAndValues(field) != value
    }
  }
}
