package net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets

import com.olvind.mui.csstype.mod.Property.FlexDirection
import com.olvind.mui.muiMaterial.components as mui
import net.wiringbits.webapp.utils.slinkyUtils.Utils.CSSPropertiesUtils
import slinky.core.facade.{Fragment, ReactElement}
import slinky.core.{FunctionalComponent, KeyAddingStage}

object Scaffold {
  case class Props(appbar: Option[ReactElement] = None, body: ReactElement, footer: Option[ReactElement] = None)

  def apply(
      appbar: Option[ReactElement] = None,
      body: ReactElement,
      footer: Option[ReactElement] = None
  ): KeyAddingStage = {
    component(Props(appbar = appbar, body = body, footer = footer))
  }

  private val scaffoldCss = new CSSPropertiesUtils {
    flex = "auto"
    display = "flex"
    flexDirection = FlexDirection.column
  }

  private val scaffoldBodyCss = new CSSPropertiesUtils {
    minHeight = "100vh"
    flex = "auto"
    display = "flex"
    flexDirection = FlexDirection.column
    padding = "1em"
  }

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val appbar: ReactElement = props.appbar match {
      case Some(e) => mui.Box(e)
      case None => Fragment()
    }

    val footer: ReactElement = props.footer match {
      case Some(e) => mui.Box(e)
      case None => Fragment()
    }

    mui
      .Box(
        appbar,
        mui.Box(props.body).sx(scaffoldBodyCss),
        footer
      )
      .sx(scaffoldCss)
  }
}
