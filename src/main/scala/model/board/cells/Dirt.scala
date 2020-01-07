package model.board.cells

import java.awt.{Color, Graphics2D, Image}
import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO
import view.View

object Dirt {
  val DIRT_IMAGE: Image = ImageIO.read(new File(View.getSourcePath("tiles/dirt_path_1.png")))
}

class Dirt extends Cell {
  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  override def getImage: BufferedImage = {
    val g2d = canvasImage.getGraphics.asInstanceOf[Graphics2D]
    g2d.drawImage(Dirt.DIRT_IMAGE, 0, 0, getObjectWidth, getObjectHeight, null)
    g2d.dispose()
    canvasImage
  }

  /** Progresses animations by one frame. Parent objects should call on all child objects they render. */
  override def advanceFrame(): Unit = {}
}
