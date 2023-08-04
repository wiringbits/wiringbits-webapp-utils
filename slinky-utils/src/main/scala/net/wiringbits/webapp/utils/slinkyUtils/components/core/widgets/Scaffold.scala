package net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets

import slinky.core.facade.{Fragment, ReactElement}
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.web.html.{className, div,style}

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
     flexDirection=FlexDirectionProperty.column
    )

    val scaffoldBody = js.Dynamic.literal(
     minHeight="100vh",
     flex="auto",
     display="flex",
     flexDirection=FlexDirectionProperty.column,
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
      div(className := "scaffoldBody",style:=scaffoldBody)(props.body),
      footer
    )
  }
}
