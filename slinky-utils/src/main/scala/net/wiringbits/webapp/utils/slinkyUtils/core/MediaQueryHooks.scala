package net.wiringbits.webapp.utils.slinkyUtils.core

import com.olvind.mui.muiMaterial.useMediaQueryMod

object MediaQueryHooks {

  def useIsLaptop(): Boolean = {
    useMediaQueryMod.default("(min-width: 769px)")
  }

  def useIsTablet(): Boolean = {
    useMediaQueryMod.default("(min-width: 426px) and (max-width: 768px)")
  }

  def useIsMobile(): Boolean = {
    useMediaQueryMod.default("(max-width: 425px)")
  }

  def useIsMobileOrTablet(): Boolean = {
    useMediaQueryMod.default("(max-width: 768px)")
  }

}
