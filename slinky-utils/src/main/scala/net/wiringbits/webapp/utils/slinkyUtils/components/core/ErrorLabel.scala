package net.wiringbits.webapp.utils.slinkyUtils.components.core

import com.olvind.mui.muiMaterial.components as mui
import com.olvind.mui.muiMaterial.stylesCreateTypographyMod.Variant
import net.wiringbits.webapp.utils.slinkyUtils.Utils.CSSPropertiesUtils
import org.scalablytyped.runtime.StringDictionary
import slinky.core.facade.Fragment
import slinky.core.{FunctionalComponent, KeyAddingStage}

object ErrorLabel {
  case class Props(text: String)

  def apply(text: String): KeyAddingStage = {
    component(Props(text))
  }

  private val errorLabelCss = new CSSPropertiesUtils {
    color = "#f44336"
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    if (props.text.nonEmpty) {
      mui
        .Typography(props.text)
        .variant(Variant.body2)
        .sx(errorLabelCss)
    } else {
      Fragment()
    }
  }
}
