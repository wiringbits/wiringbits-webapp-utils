package net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets

import com.olvind.mui.csstype.mod.DataType.DisplayInside
import com.olvind.mui.csstype.mod.Property.{BoxSizing, FlexDirection}
import com.olvind.mui.muiMaterial.components as mui
import net.wiringbits.webapp.utils.slinkyUtils.Utils.CSSPropertiesUtils
import slinky.core.facade.ReactElement
import slinky.core.{FunctionalComponent, KeyAddingStage}

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
    val borderRadius_ = props.borderRadius.getOrElse("0px")
    val minWidth_ = props.minWidth.getOrElse("0")
    val maxWidth_ = props.maxWidth.getOrElse("auto")

    val flex_ = props.flex.map(_.toString).getOrElse("none")

    val containerCss = new CSSPropertiesUtils {
      display = DisplayInside.flex
      boxSizing = BoxSizing.`border-box`
      width = "auto"
      margin = props.margin.value()
      padding = props.padding.value()
      borderRadius = borderRadius_
      minWidth = minWidth_
      maxWidth = maxWidth_
      flex = flex_
      flexDirection = props.flexDirection
      alignItems = parseAlignment(props.alignItems)
      justifyContent = parseAlignment(props.justifyContent)
    }

    mui.Box(props.child).sx(containerCss)
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
