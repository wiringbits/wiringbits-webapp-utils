package net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets

import com.alexitc.materialui.facade.materialUiCore.mod.PropTypes.Color
import com.alexitc.materialui.facade.materialUiCore.{typographyTypographyMod, components => mui}
import slinky.core.{FunctionalComponent, KeyAddingStage}

object Title {
  case class Props(text: String, color: Option[Color] = None)

  def apply(text: String, color: Option[Color] = None): KeyAddingStage = {
    component(Props(text = text, color = color))
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val color = props.color.getOrElse(Color.inherit)

    mui
      .Typography()
      .color(color)
      .variant(typographyTypographyMod.Style.h4)(props.text)
  }
}
