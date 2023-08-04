package net.wiringbits.webapp.utils.slinkyUtils.components.core

import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.web.html.{style,className, div, h1}
import com.olvind.mui.muiIconsMaterial.components as muiIcons
import com.olvind.mui.csstype.mod.Property.FlexDirection


object ErrorBoundaryInfo {
  case class Props(error: scala.scalajs.js.Error)

  def apply(error: scala.scalajs.js.Error): KeyAddingStage = {
    component(Props(error))
  }

    val errorBoundaryInfoStyle = js.Dynamic.literal(
     display="flex",
     flexDirection=FlexDirection.column,
     alignItems="center",
     justifyContent="center"
    )
    val contentStyle = js.Dynamic.literal(
     display="flex",
     flexDirection=FlexDirection.column
    )      
    val iconStyle = js.Dynamic.literal(
       display="flex",
       justifyContent="center"
        //"& svg ",
        //    CSSProperties()
        //      .setFontSize("4em")
      )


  
  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    val e = props.error

    div(
      className := "errorBoundaryInfo", style:=errorBoundaryInfoStyle,
      div(
        className := "content",style:=contentStyle,
        div(className := "icon",style:=iconStyle, muiIcons.Warning()),
        h1("You hit an unexpected error"),
        div(e.toString)
      )
    )
  }
}
