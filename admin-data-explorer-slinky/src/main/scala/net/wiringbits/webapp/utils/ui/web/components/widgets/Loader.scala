package net.wiringbits.webapp.utils.ui.web.components.widgets

import com.alexitc.materialui.facade.materialUiCore.{components => mui, materialUiCoreStrings => muiStrings}
import net.wiringbits.webapp.utils.slinkyUtils.components.core.widgets.{CircularLoader, Container}
import net.wiringbits.webapp.utils.ui.web.AppStrings
import slinky.core.facade.Fragment
import slinky.core.{FunctionalComponent, KeyAddingStage}

object Loader {
  type Props = Unit

  def apply(): KeyAddingStage = component(())

  val component: FunctionalComponent[Props] = FunctionalComponent[Props] { props =>
    Container(
      flex = Some(1),
      alignItems = Container.Alignment.center,
      justifyContent = Container.Alignment.center,
      child = Fragment(
        CircularLoader(),
        mui.Typography(AppStrings.loading).variant(muiStrings.h6)
      )
    )
  }
}
