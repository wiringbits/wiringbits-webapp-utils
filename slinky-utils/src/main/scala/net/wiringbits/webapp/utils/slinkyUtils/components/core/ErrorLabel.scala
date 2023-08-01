package net.wiringbits.webapp.utils.slinkyUtils.components.core

import com.olvind.mui.StBuildingComponent
import com.olvind.mui.muiIconsMaterial.components as muiIcons
import com.olvind.mui.muiMaterial.colorsMod.{deepOrange, deepPurple}
import com.olvind.mui.muiMaterial.components as mui
import com.olvind.mui.muiMaterial.stylesCreateThemeMod.Theme
import com.olvind.mui.muiSystem.styleFunctionSxStyleFunctionSxMod.SystemCssProperties
import com.olvind.mui.react.mod.{DetailedHTMLProps, HTMLAttributes}
import com.olvind.mui.react.components.div
import org.scalablytyped.runtime.StringDictionary
import slinky.core.facade.Fragment
import slinky.core.{FunctionalComponent, KeyAddingStage}

object ErrorLabel {

  case class Props(text: String)
  val newStyle = new SystemCssProperties[Theme]{
      color="#f44336"
  }
  def apply(text: String): KeyAddingStage = {
    component(Props(text))
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>

    if (props.text.nonEmpty) {
      Fragment(
        mui
          .Typography(props.text)
          .className("errorLabel")
          .sx(newStyle)
      )
    } else {
      Fragment()
    }
  }
}
