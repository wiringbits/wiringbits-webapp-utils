package net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets

import com.olvind.mui.muiMaterial.components as mui
import net.wiringbits.webapp.utils.slinkyUtils.Utils.CSSPropertiesUtils
import slinky.core.{FunctionalComponent, KeyAddingStage}

object CircularLoader {
  case class Props(size: Int = 16)

  def apply(size: Int = 16): KeyAddingStage = {
    component(Props(size = size))
  }

  private val circularLoaderCss = new CSSPropertiesUtils {
    display = "flex"
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    mui.CircularProgress
      .sx(circularLoaderCss)
      .size(props.size)
  }
}
