package view.gui

import java.awt.image.BufferedImage

sealed trait MenuItem {
  val guiAction: GuiAction
  val isSelectable: Boolean
}

case class BasicMenuItem(text: String, guiAction: GuiAction, isSelectable: Boolean = true)
  extends MenuItem

case class ImageItem(image: BufferedImage, width: Int, height: Int, guiAction: GuiAction, isSelectable: Boolean = true)
  extends MenuItem
