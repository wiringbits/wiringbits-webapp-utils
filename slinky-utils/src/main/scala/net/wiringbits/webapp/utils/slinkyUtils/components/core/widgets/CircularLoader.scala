package net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets

import slinky.core.{FunctionalComponent, KeyAddingStage}
import com.olvind.mui.muiMaterial.components as mui
import com.olvind.mui.muiMaterial.stylesCreateThemeMod.Theme
import com.olvind.mui.muiSystem.styleFunctionSxStyleFunctionSxMod.SystemCssProperties

object CircularLoader {
  case class Props(size: Int = 16)

  def apply(size: Int = 16): KeyAddingStage = {
    component(Props(size = size))
  }
  
  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    mui
      .CircularProgress()
      .className("circularLoader")
      .sx(new SystemCssProperties[Theme]{
        display="flex"
      })
      .size(props.size)
  }
}
