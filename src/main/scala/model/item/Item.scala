package model.item

import java.awt.{Graphics2D, Image}
import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO
import model.board.BoardObject
import view.View

object Item {
  //TODO get the actual pokeball avatar.
  /** Returns the avatar used for the pokeball. */
  def getPokeballImage: Image = ImageIO.read(new File(View.getSourcePath("frog.jpg")))
}

class Item extends BoardObject {
  protected val image: Image = Item.getPokeballImage
  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  override def getImage: BufferedImage = {
    val g2d = canvasImage.getGraphics.asInstanceOf[Graphics2D]
    g2d.drawImage(image, 0, 0, getObjectWidth, getObjectHeight, null)
    g2d.dispose()
    canvasImage
  }

  //TODO prescaled images for items.
  /** Returns the object's image, already scaled. This is to speed up rendering. */
  override def getPrescaledImage: Option[BufferedImage] = None

  /** Progresses animations by one frame. Parent objects should call on all child objects they render. */
  override def advanceFrame(): Unit = {}
}
