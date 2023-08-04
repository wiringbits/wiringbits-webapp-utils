package net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets

import com.olvind.mui.csstype.mod.Property.BoxSizing
import slinky.core.facade.ReactElement
import slinky.core.{FunctionalComponent, KeyAddingStage}
import slinky.web.html.{className, div, style}

import scala.scalajs.js

object Container {
  case class Props(
      child: ReactElement,
      margin: EdgeInsets = EdgeInsets.all(0),
      padding: EdgeInsets = EdgeInsets.all(0),
      borderRadius: Option[String] = None,
      flex: Option[Int] = None,
      flexDirection: FlexDirection = FlexDirection.column,
      alignItems: Alignment = Alignment.flexStart,
      justifyContent: Alignment = Alignment.flexStart,
      minWidth: Option[String] = None,
      maxWidth: Option[String] = None
  )
  
  def apply(
      child: ReactElement,
      margin: EdgeInsets = EdgeInsets.all(0),
      padding: EdgeInsets = EdgeInsets.all(0),
      borderRadius: Option[String] = None,
      flex: Option[Int] = None,
      flexDirection: FlexDirection = FlexDirection.column,
      alignItems: Alignment = Alignment.flexStart,
      justifyContent: Alignment = Alignment.flexStart,
      minWidth: Option[String] = None,
      maxWidth: Option[String] = None
  ): KeyAddingStage = {
    component(
      Props(
        child = child,
        margin = margin,
        padding = padding,
        borderRadius = borderRadius,
        flex = flex,
        flexDirection = flexDirection,
        alignItems = alignItems,
        justifyContent = justifyContent,
        minWidth = minWidth,
        maxWidth = maxWidth
      )
    )
  }

  sealed trait FlexDirection extends Product with Serializable

  object FlexDirection {
    case object column extends FlexDirection
    case object row extends FlexDirection
  }

  sealed trait Alignment extends Product with Serializable

  object Alignment extends Enumeration {
    case object center extends Alignment
    case object flexStart extends Alignment
    case object flexEnd extends Alignment
    case object spaceBetween extends Alignment
    case object spaceAround extends Alignment
    case object spaceEvenly extends Alignment
  }

  case class EdgeInsets(top: Int, right: Int, bottom: Int, left: Int) {
    def value() = s"${top}px ${right}px ${bottom}px ${left}px"
  }

  object EdgeInsets {
    def all(value: Int): EdgeInsets = EdgeInsets(value, value, value, value)
    def top(value: Int): EdgeInsets = EdgeInsets(value, 0, 0, 0)
    def right(value: Int): EdgeInsets = EdgeInsets(0, value, 0, 0)
    def bottom(value: Int): EdgeInsets = EdgeInsets(0, 0, value, 0)
    def left(value: Int): EdgeInsets = EdgeInsets(0, 0, 0, value)
    def horizontal(value: Int): EdgeInsets = EdgeInsets(0, value, 0, value)
    def vertical(value: Int): EdgeInsets = EdgeInsets(value, 0, value, 0)
  }


  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>


    val borderRadius = props.borderRadius.getOrElse("0px")
    val minWidth = props.minWidth.getOrElse("0")
    val maxWidth = props.maxWidth.getOrElse("auto")

    val flex = props.flex.getOrElse("none")

    val containerStyle = js.Dynamic.literal(
      margin = props.margin.value(),
      padding = props.padding.value(),
      borderRadius = borderRadius,
      minWidth = minWidth,
      maxWidth = maxWidth,
      flex = flex.toString,
      flexDirection = props.flexDirection.toString,
      alignItems = parseAlignment(props.alignItems),
      justifyContent = parseAlignment(props.justifyContent),
      width="auto",
      boxSizing=BoxSizing.`border-box`,
      display="flex"
    )

    div(className := "container", style := containerStyle)(props.child)
  }

  private def parseAlignment(alignment: Alignment): String = {
    alignment match {
      case Alignment.center => "center"
      case Alignment.flexStart => "flex-start"
      case Alignment.flexEnd => "flex-end"
      case Alignment.spaceAround => "space-around"
      case Alignment.spaceBetween => "space-between"
      case Alignment.spaceEvenly => "space-evenly"
    }
  }
}
