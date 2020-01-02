package view.gui

case class GuiAction(functionToCall: () => Unit = () => println("GuiAction not yet implemented."))
