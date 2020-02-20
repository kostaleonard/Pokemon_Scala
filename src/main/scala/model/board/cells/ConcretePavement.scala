package model.board.cells

import java.awt.{Color, Graphics2D, Image}
import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO
import model.board.Board
import view.View

object ConcretePavement {
  val CONCRETE_PAVEMENT_IMAGE: Image =
    ImageIO.read(new File(View.getSourcePath("tiles/concrete_path_1.png")))
}

class ConcretePavement extends Cell {
  val prescaledImage: BufferedImage = getPrescaledImage.get

  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  override def getImage: BufferedImage = prescaledImage

  /** Returns the object's image, already scaled. This is to speed up rendering. */
  override def getPrescaledImage: Option[BufferedImage] = {
    val bufferedImage = new BufferedImage(Board.TILE_SIZE, Board.TILE_SIZE, BufferedImage.TYPE_INT_ARGB)
    val g2d = bufferedImage.getGraphics.asInstanceOf[Graphics2D]
    g2d.drawImage(ConcretePavement.CONCRETE_PAVEMENT_IMAGE, 0, 0, Board.TILE_SIZE, Board.TILE_SIZE, null)
    g2d.dispose()
    Some(bufferedImage)
  }
}
