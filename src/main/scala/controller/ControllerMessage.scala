package controller

import view.View

sealed trait ControllerMessage

case class SwitchViews(nextView: View) extends ControllerMessage

case class SendKeyPress(keyCode: Int) extends ControllerMessage
