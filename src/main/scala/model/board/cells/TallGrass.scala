package model.board.cells

import java.awt.{Color, Graphics2D}
import java.awt.image.BufferedImage

class TallGrass extends Cell {
  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  override def getImage: BufferedImage = {
    val g2d = canvasImage.getGraphics.asInstanceOf[Graphics2D]
    g2d.setColor(Color.GREEN.darker().darker())
    g2d.fillRect(0, 0, getObjectWidth, getObjectHeight)
    g2d.dispose()
    canvasImage
  }
}
