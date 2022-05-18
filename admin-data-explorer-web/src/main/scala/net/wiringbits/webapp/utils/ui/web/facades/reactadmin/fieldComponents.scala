package net.wiringbits.webapp.utils.ui.web.facades.reactadmin

import japgolly.scalajs.react.vdom.VdomNode
import io.github.nafg.simplefacade.Implicits._
import io.github.nafg.simplefacade.{FacadeModule, PropTypes}

trait FieldProps extends PropTypes {
  val source = of[String]
  val disabled = of[Boolean]
}

object EmailField extends FacadeModule.Simple {
  override def raw = ReactAdmin.EmailField
  class Props extends FieldProps
  override def mkProps = new Props
}

object ReferenceField extends FacadeModule.ArrayChildren.Simple {
  override def raw = ReactAdmin.ReferenceField
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children = of[VdomNode]
    val source = of[String]
    val reference = of[String]
  }
  override def mkProps = new Props
}

object ReferenceInput extends FacadeModule.ArrayChildren.Simple {
  override def raw = ReactAdmin.ReferenceInput
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children = of[VdomNode]
    val source = of[String]
    val reference = of[String]
  }
  override def mkProps = new Props
}

object SelectInput extends FacadeModule.Simple {
  override def raw = ReactAdmin.SelectInput
  class Props extends PropTypes {
    val optionText = of[String]
  }
  override def mkProps = new Props
}

object TextField extends FacadeModule.Simple {
  override def raw = ReactAdmin.TextField
  class Props extends FieldProps
  override def mkProps = new Props
}

object DateField extends FacadeModule.Simple {
  override def raw = ReactAdmin.DateField
  class Props extends FieldProps
  override def mkProps = new Props
}

object TextInput extends FacadeModule.Simple {
  override def raw = ReactAdmin.TextInput
  class Props extends FieldProps {
    val multiline = of[Boolean]
  }
  override def mkProps = new Props
}

object UrlField extends FacadeModule.Simple {
  override def raw = ReactAdmin.UrlField
  class Props extends FieldProps
  override def mkProps = new Props
}
