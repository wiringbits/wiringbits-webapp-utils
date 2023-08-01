package net.wiringbits.webapp.utils.slinkyUtils.core
import com.olvind.mui.muiMaterial.useMediaQueryMod
//import com.alexitc.materialui.facade.materialUiCore.useMediaQueryMod.unstableUseMediaQuery



object MediaQueryHooks {

  def useIsLaptop() = {
    useMediaQueryMod.default("(min-width: 769px)")
  }

  def useIsTablet() = {
    useMediaQueryMod.default("(min-width: 426px) and (max-width: 768px)")
  }

  def useIsMobile() = {
    useMediaQueryMod.default("(max-width: 425px)")
  }

  def useIsMobileOrTablet() = {
    useMediaQueryMod.default("(max-width: 768px)")
  }

}
