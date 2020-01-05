package view.views

import java.awt.{Color, Graphics2D}
import java.awt.image.BufferedImage

import controller.SwitchViews
import model.Model
import view.View

class OverworldView(override protected val model: Model) extends View(model) {
  /** The action taken when a key is pressed and the View is in focus. */
  override def keyPressed(keyCode: Int): Unit = {}

  /** The action taken when a key is released and the View is in focus. */
  override def keyReleased(keyCode: Int): Unit = {}

  /** The action taken when a key is typed and the View is in focus. */
  override def keyTyped(keyCode: Int): Unit = {}

  /** The action taken when a key is held and the View is in focus. */
  override def keyHeld(keyCode: Int): Unit = {}

  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  override def getImage: BufferedImage = {
    val g2d = canvasImage.getGraphics.asInstanceOf[Graphics2D]
    val boardImage = model.getCurrentBoard.getImage
    g2d.drawImage(boardImage, 0, 0, boardImage.getWidth, boardImage.getHeight, null)
    g2d.dispose()
    canvasImage
  }
}