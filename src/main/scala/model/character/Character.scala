package model.character

import java.awt.{Color, Graphics2D, Image}
import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO
import model.board.{Board, BoardObject}
import view.View

object Character {
  //TODO get the actual player avatar.
  /** Returns the avatar used for the player character. */
  def getPlayerAvatar: Image = ImageIO.read(new File(View.getSourcePath("frog.jpg")))
}

class Character extends BoardObject {
  //TODO you're going to have to end up defining classes of avatars to handle animations, then make it a constructor argument.
  protected var avatar: Image = Character.getPlayerAvatar
  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  override def getImage: BufferedImage = {
    val g2d = canvasImage.getGraphics.asInstanceOf[Graphics2D]
    g2d.drawImage(avatar, 0, 0, getObjectWidth, getObjectHeight, null)
    g2d.dispose()
    canvasImage
  }
}
