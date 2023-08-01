package net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets

import com.olvind.mui.muiMaterial.components as mui
import com.olvind.mui.muiMaterial.typographyTypographyMod
import com.olvind.mui.muiMaterial
import com.olvind.mui.muiMaterial.mod.PropTypes.Color
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
      .variant("h4")
      .color(color)(props.text)
  }
}
