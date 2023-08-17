package net.wiringbits.webapp.utils.slinkyUtils.components.core

import com.olvind.mui.muiMaterial.components as mui
import com.olvind.mui.muiMaterial.stylesCreateThemeMod.Theme
import com.olvind.mui.muiSystem.styleFunctionSxStyleFunctionSxMod.SystemCssProperties
import com.olvind.mui.csstype.mod.Property.FlexDirection
import com.olvind.mui.react.mod.CSSProperties
import slinky.core.facade.Fragment
import slinky.core.{FunctionalComponent, KeyAddingStage, TagMod}
import slinky.web.html.{div, style}

object InfoCard {
  case class Props(message: String, icon: TagMod[div.tag.type], child: Option[TagMod[div.tag.type]] = None)

  def apply(message: String, icon: TagMod[div.tag.type], child: Option[TagMod[div.tag.type]] = None): KeyAddingStage = {
    component(Props(message = message, icon = icon, child = child))
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    mui.Paper
      .className("infoCard")
      .sx(new SystemCssProperties[Theme]{
           minHeight=200
           display="flex"
           flexDirection=FlexDirection.column
           alignItems="center"
           justifyContent="center"
           fontSize="1.5em"
           borderRadius=8
           padding=16
           overflow="hidden"
           color="#616161"
        })
      .elevation(0)(
        div(props.icon,style := new CSSProperties{
          fontSize="2em"
          marginBottom=16
        }),
        props.message,
        props.child.getOrElse(""),
      )
  }
}
