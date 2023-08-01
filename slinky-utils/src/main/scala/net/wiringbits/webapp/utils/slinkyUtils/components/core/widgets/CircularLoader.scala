package net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets

import org.scalablytyped.runtime.StringDictionary
import slinky.core.{FunctionalComponent, KeyAddingStage}
import com.olvind.mui.muiMaterial.components as mui
//import com.olvind.mui.muiMaterial.mod.{makeStyles,withStyles}
import com.olvind.mui.react.mod.CSSProperties
import com.olvind.mui.muiMaterial.stylesCreateThemeMod.Theme
import com.olvind.mui.muiMaterial.stylesMod.useTheme
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|
import com.olvind.mui.muiSystem.styleFunctionSxStyleFunctionSxMod.SystemCssProperties

object CircularLoader {
  case class Props(size: Int = 16)

  def apply(size: Int = 16): KeyAddingStage = {
    component(Props(size = size))
  }

  val newStyle = new SystemCssProperties[Theme]{
      display="flex"
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    mui
      .CircularProgress()
      .className("circularLoader")
      .sx(newStyle)
      .size(props.size)

  }
}
