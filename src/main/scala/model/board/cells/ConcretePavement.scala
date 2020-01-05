package model.board.cells

import java.awt.{Color, Graphics2D, Image}
import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO
import view.View

object ConcretePavement {
  val CONCRETE_PAVEMENT_IMAGE: Image =
    ImageIO.read(new File(View.getSourcePath("tiles/concrete_path_1.png")))
}

class ConcretePavement extends Cell {
  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  override def getImage: BufferedImage = {
    val g2d = canvasImage.getGraphics.asInstanceOf[Graphics2D]
    g2d.drawImage(ConcretePavement.CONCRETE_PAVEMENT_IMAGE, 0, 0, getObjectWidth, getObjectHeight, null)
    g2d.dispose()
    canvasImage
  }
}
