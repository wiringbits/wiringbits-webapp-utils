package net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets

import slinky.core.facade.{Fragment, ReactElement}
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.web.html.{className, div, style}
import com.olvind.mui.csstype.mod.Property.FlexDirection
import scala.scalajs.js

object Scaffold {
  case class Props(appbar: Option[ReactElement] = None, body: ReactElement, footer: Option[ReactElement] = None)

  def apply(
      appbar: Option[ReactElement] = None,
      body: ReactElement,
      footer: Option[ReactElement] = None
  ): KeyAddingStage = {
    component(Props(appbar = appbar, body = body, footer = footer))
  }

  val scaffoldStyle = js.Dynamic.literal(
   flex="auto",
   display="flex",
   flexDirection=FlexDirection.column
  )

  val scaffoldBodyStyle = js.Dynamic.literal(
   minHeight="100vh",
   flex="auto",
   display="flex",
   flexDirection=FlexDirection.column,
   padding="1em"
  )
val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>


    val appbar = props.appbar match {
      case Some(e) => Fragment(div(className := "scaffoldAppbar")(e))
      case None => Fragment()
    }

    val footer = props.footer match {
      case Some(e) => Fragment(div(className := "scaffoldFooter")(e))
      case None => Fragment()
    }

    div(className := "scaffold",style:=scaffoldStyle)(
      appbar,
      div(className := "scaffoldBody",style:=scaffoldBodyStyle)(props.body),
      footer
    )
  }
}
