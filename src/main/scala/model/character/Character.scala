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
  def getPlayerAvatar: Image =
    ImageIO.read(new File(View.getSourcePath("sprites/player/player_front.png")))
}

class Character extends BoardObject {
  //TODO you're going to have to end up defining classes of avatars to handle animations, then make it a constructor argument.
  protected var avatar: Image = Character.getPlayerAvatar
  protected var drawOffsetX = 0
  protected var drawOffsetY = 0

  /** Returns the Character's x offset for drawing. */
  def getDrawOffsetX: Int = drawOffsetX

  /** Returns the Character's y offset for drawing. */
  def getDrawOffsetY: Int = drawOffsetY

  /** Sets the Character's x offset for drawing. */
  def setDrawOffsetX(off: Int): Unit = drawOffsetX = off

  /** Sets the Character's y offset for drawing. */
  def setDrawOffsetY(off: Int): Unit = drawOffsetY = off

  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  override def getImage: BufferedImage = {
    val g2d = canvasImage.getGraphics.asInstanceOf[Graphics2D]
    g2d.drawImage(avatar, 0, 0, (getObjectWidth * 0.8).toInt, getObjectHeight, null)
    g2d.dispose()
    canvasImage
  }

  /** Progresses animations by one frame. Parent objects should call on all child objects they render. */
  override def advanceFrame(): Unit = {
    if(drawOffsetX > 0) drawOffsetX -= 1
    else if(drawOffsetX < 0) drawOffsetX += 1
    if(drawOffsetY > 0) drawOffsetY -= 1
    else if(drawOffsetY < 0) drawOffsetY += 1
  }
}
