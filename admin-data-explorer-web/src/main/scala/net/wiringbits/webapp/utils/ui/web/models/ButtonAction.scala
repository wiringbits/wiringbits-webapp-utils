package net.wiringbits.webapp.utils.ui.web.models

/** @param text
  *   button label that's going to be displayed in react-admin
  * @param onClick
  *   this callback has the resource ID of the clicked element
  */
case class ButtonAction(text: String, onClick: String => Unit)
