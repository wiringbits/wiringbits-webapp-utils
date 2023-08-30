package net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets

import com.olvind.mui.csstype.mod.DataType.Color
import com.olvind.mui.muiMaterial.components as mui
import com.olvind.mui.muiMaterial.stylesCreateTypographyMod.Variant
import slinky.core.{FunctionalComponent, KeyAddingStage}

object Title {
  case class Props(text: String, color: Option[Color] = None)

  def apply(text: String, color: Option[Color] = None): KeyAddingStage = {
    component(Props(text = text, color = color))
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val color = props.color.getOrElse("inherit")

    mui
      .Typography()
      .color(color)
      .variant(Variant.h4)(props.text)
  }
}
