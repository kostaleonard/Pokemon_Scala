package model.board.cells

import java.awt.{Color, Graphics2D, Image}
import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO
import model.board.Board
import view.View

object LowGrass {
  val LOW_GRASS_IMAGE: Image = ImageIO.read(new File(View.getSourcePath("tiles/low_grass_1.png")))

  /** Returns the prescaled image. */
  protected def getPrescaledImage: BufferedImage = {
    val bufferedImage = new BufferedImage(Board.TILE_SIZE, Board.TILE_SIZE, BufferedImage.TYPE_INT_ARGB)
    val g2d = bufferedImage.getGraphics.asInstanceOf[Graphics2D]
    g2d.drawImage(LOW_GRASS_IMAGE, 0, 0, Board.TILE_SIZE, Board.TILE_SIZE, null)
    g2d.dispose()
    bufferedImage
  }
}

class LowGrass extends Cell {
  val prescaledImage: BufferedImage = LowGrass.getPrescaledImage

  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  override def getImage: BufferedImage = prescaledImage

  /** Progresses animations by one frame. Parent objects should call on all child objects they render. */
  override def advanceFrame(): Unit = {}
}
