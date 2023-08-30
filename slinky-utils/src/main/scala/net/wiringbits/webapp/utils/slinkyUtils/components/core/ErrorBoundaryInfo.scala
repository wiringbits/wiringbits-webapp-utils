package net.wiringbits.webapp.utils.slinkyUtils.components.core

import com.olvind.mui.csstype.mod.DataType.{ContentPosition, DisplayInside, SelfPosition}
import com.olvind.mui.csstype.mod.Property.FlexDirection
import com.olvind.mui.muiIconsMaterial.components as muiIcons
import com.olvind.mui.muiMaterial.components as mui
import com.olvind.mui.muiMaterial.stylesCreateTypographyMod.Variant
import net.wiringbits.webapp.utils.slinkyUtils.Utils.CSSPropertiesUtils
import slinky.core.{FunctionalComponent, KeyAddingStage}

object ErrorBoundaryInfo {
  case class Props(error: scala.scalajs.js.Error)

  def apply(error: scala.scalajs.js.Error): KeyAddingStage = {
    component(Props(error))
  }

  private val errorBoundaryInfoCss = new CSSPropertiesUtils {
    flex = "auto"
    display = DisplayInside.flex
    flexDirection = FlexDirection.column
    alignItems = SelfPosition.center
    justifyContent = ContentPosition.center
    alignItems = ContentPosition.center
    justifyContent = ContentPosition.center
  }

  private val contentCss = new CSSPropertiesUtils {
    display = DisplayInside.flex
    flexDirection = FlexDirection.column
  }

  private val iconCss = new CSSPropertiesUtils {
    display = DisplayInside.flex
    justifyContent = ContentPosition.center
  }.set(
    "& svg",
    new CSSPropertiesUtils {
      fontSize = "4em"
    }
  )

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val e = props.error

    mui
      .Box(
        mui
          .Box(
            mui.Box(muiIcons.Warning()).sx(iconCss),
            mui.Typography("You hit an unexpected error").variant(Variant.h1),
            mui.Box(e.toString)
          )
          .sx(contentCss)
      )
      .sx(errorBoundaryInfoCss)
  }
}
