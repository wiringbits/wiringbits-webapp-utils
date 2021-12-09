package net.wiringbits.webapp.utils.slinkyUtils.components.core

import slinky.core.facade.{Fragment, ReactElement}
import slinky.core.{FunctionalComponent, KeyAddingStage}

object ConditionalComponent {

  case class Props(
      condition: Boolean,
      whenTrue: () => ReactElement = () => Fragment(),
      whenFalse: () => ReactElement = () => Fragment()
  )

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    Option
      .when(props.condition)(props.whenTrue())
      .getOrElse(props.whenFalse())
  }

  def apply(condition: Boolean)(
      whenTrue: => ReactElement = Fragment(),
      whenFalse: => ReactElement = Fragment()
  ): KeyAddingStage = {
    component(Props(condition, () => whenTrue, () => whenFalse))
  }
}
