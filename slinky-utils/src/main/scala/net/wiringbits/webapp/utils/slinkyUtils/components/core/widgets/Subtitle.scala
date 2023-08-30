package net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets

import com.olvind.mui.csstype.mod.Property.Color
import com.olvind.mui.muiMaterial.components as mui
import com.olvind.mui.muiMaterial.stylesCreateTypographyMod.Variant
import slinky.core.{FunctionalComponent, KeyAddingStage}

object Subtitle {
  case class Props(text: String, color: Option[Color] = None)

  def apply(text: String, color: Option[Color] = None): KeyAddingStage = {
    component(Props(text = text, color = color))
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val color = props.color.getOrElse("inherit")

    mui
      .Typography()
      .color(color)
      .variant(Variant.h6)(props.text)
  }
}
