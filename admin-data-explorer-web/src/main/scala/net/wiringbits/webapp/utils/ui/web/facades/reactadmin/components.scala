package net.wiringbits.webapp.utils.ui.web.facades.reactadmin

import io.github.nafg.simplefacade.Implicits._
import io.github.nafg.simplefacade.{FacadeModule, PropTypes}
import japgolly.scalajs.react.vdom.html_<^.VdomNode

object Admin extends FacadeModule.NodeChildren.Simple {
  override def raw = ReactAdmin.Admin
  override def mkProps = new Props
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children = of[VdomNode]
    val dataProvider = of[DataProvider]
  }
}

object Create extends FacadeModule.NodeChildren.Simple {
  override def raw = ReactAdmin.Create
  override def mkProps = new Props
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children = of[VdomNode]
  }
}

object Datagrid extends FacadeModule.ArrayChildren.Simple {
  override def raw = ReactAdmin.Datagrid
  override def mkProps = new Props
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children = of[VdomNode]
    val rowClick = of[String]
    val bulkActionButtons = of[Boolean]
  }
}

object Edit extends FacadeModule.NodeChildren.Simple {
  override def raw = ReactAdmin.Edit
  override def mkProps = new Props
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children = of[VdomNode]
    val actions = of[VdomNode]
  }
}

object EditButton extends FacadeModule.Simple {
  override def raw = ReactAdmin.EditButton
  override def mkProps = new Props
  class Props extends PropTypes
}

object SaveButton extends FacadeModule.Simple {
  override def raw = ReactAdmin.SaveButton
  override def mkProps = new Props
  class Props extends PropTypes
}

object DeleteButton extends FacadeModule.Simple {
  override def raw = ReactAdmin.DeleteButton
  override def mkProps = new Props
  class Props extends PropTypes
}

object Button extends FacadeModule.NodeChildren.Simple {
  override def raw = ReactAdmin.Button
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children = of[VdomNode]
    val onClick = of[() => Unit]
  }
  override def mkProps = new Props
}

object Toolbar extends FacadeModule.NodeChildren.Simple {
  override def raw = ReactAdmin.Toolbar
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children = of[VdomNode]
  }
  override def mkProps = new Props
}

object TopToolbar extends FacadeModule.NodeChildren.Simple {
  override def raw = ReactAdmin.TopToolbar
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children = of[VdomNode]
  }
  override def mkProps = new Props
}

object ComponentList extends FacadeModule.NodeChildren.Simple {
  override def raw = ReactAdmin.List
  override def mkProps = new Props
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children = of[VdomNode]
  }
}

object Resource extends FacadeModule.Simple {
  override def raw = ReactAdmin.Resource
  override def mkProps = new Props
  class Props extends PropTypes {
    val name = of[String]
    val create, edit, list = of[VdomNode]
  }
}

object SimpleForm extends FacadeModule.NodeChildren.Simple {
  override def raw = ReactAdmin.SimpleForm
  override def mkProps = new Props
  class Props extends PropTypes.WithChildren[VdomNode] {
    val children = of[VdomNode]
    val toolbar = of[VdomNode]
  }
}
