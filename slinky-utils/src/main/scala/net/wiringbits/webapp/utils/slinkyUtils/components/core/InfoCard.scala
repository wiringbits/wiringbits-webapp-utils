package net.wiringbits.webapp.utils.slinkyUtils.components.core

import com.olvind.mui.csstype.mod.DataType.{ContentPosition, DisplayInside}
import com.olvind.mui.csstype.mod.Property.{FlexDirection, OverflowInline}
import com.olvind.mui.muiMaterial.components as mui
import net.wiringbits.webapp.utils.slinkyUtils.Utils.CSSPropertiesUtils
import slinky.core.{FunctionalComponent, KeyAddingStage, TagMod}
import slinky.web.html.div

object InfoCard {
  case class Props(message: String, icon: TagMod[div.tag.type], child: Option[TagMod[div.tag.type]] = None)

  def apply(message: String, icon: TagMod[div.tag.type], child: Option[TagMod[div.tag.type]] = None): KeyAddingStage = {
    component(Props(message = message, icon = icon, child = child))
  }

  private val infoCardCss = new CSSPropertiesUtils {
    minHeight = 200
    display = DisplayInside.flex
    flexDirection = FlexDirection.column
    alignItems = ContentPosition.center
    justifyContent = ContentPosition.center
    fontSize = "1.5em"
    borderRadius = 8
    padding = 16
    overflow = OverflowInline.hidden
    color = "#616161"
  }.set(
    "& svg",
    new CSSPropertiesUtils {
      fontSize = "2em"
      marginBottom = 16
    }
  )

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    mui.Paper
      .elevation(0)(
        props.icon,
        props.message,
        props.child.getOrElse("")
      )
      .sx(infoCardCss)
  }
}
