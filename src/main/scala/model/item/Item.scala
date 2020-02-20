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

//TODO this is messed up.
abstract class Item extends BoardObject {
  protected val image: Image = Item.getPokeballImage
  protected val prescaledImage: BufferedImage = getPrescaledImage.get

  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  override def getImage: BufferedImage = prescaledImage

  //TODO prescaled images for items.
  /** Returns the object's image, already scaled. This is to speed up rendering. */
  override def getPrescaledImage: Option[BufferedImage] = None

  /** Progresses animations by one frame. Parent objects should call on all child objects they render. */
  override def advanceFrame(): Unit = {}
}
