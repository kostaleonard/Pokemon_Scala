package model.board.cells

import java.awt.{Color, Graphics2D, Image}
import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO
import view.View

object TallGrass {
  val TALL_GRASS_IMAGE: Image = ImageIO.read(new File(View.getSourcePath("tiles/tall_grass_1.png")))
}

class TallGrass extends Cell {
  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  override def getImage: BufferedImage = {
    val g2d = canvasImage.getGraphics.asInstanceOf[Graphics2D]
    g2d.drawImage(TallGrass.TALL_GRASS_IMAGE, 0, 0, getObjectWidth, getObjectHeight, null)
    g2d.dispose()
    canvasImage
  }
}
