package model.character

import java.awt.{Color, Graphics2D, Image}
import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO
import model.board.{Board, BoardObject, Direction}
import view.View

object Character {
  val MOVE_SPEED = 4
  //TODO get the actual player avatar.
  /** Returns the avatar used for the player character. */
  def getPlayerAvatar: Image =
    ImageIO.read(new File(View.getSourcePath("sprites/player/player_front.png")))
}

class Character extends BoardObject {
  //TODO you're going to have to end up defining classes of avatars to handle animations, then make it a constructor argument.
  protected var avatar: Image = Character.getPlayerAvatar
  protected var queuedMove: Option[() => Unit] = None

  /** Returns true if the character is moving. */
  def isMoving: Boolean = drawOffsetX != 0 || drawOffsetY != 0

  /** Returns true if the character is almost done moving so that the next move can be scheduled. This is just for
    * player quality of life. */
  def isAlmostDoneMoving: Boolean =
    math.abs(drawOffsetX) < 8 * Character.MOVE_SPEED &&
    math.abs(drawOffsetY) < 8 * Character.MOVE_SPEED

  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  override def getImage: BufferedImage = {
    val g2d = canvasImage.getGraphics.asInstanceOf[Graphics2D]
    g2d.drawImage(avatar, 0, 0, (getObjectWidth * 0.8).toInt, getObjectHeight, null)
    g2d.dispose()
    canvasImage
  }

  /** Queues the given move. The move function will be called when the player finishes the current move. */
  def queueMove(moveFunction: () => Unit): Unit = queuedMove = Some(moveFunction)

  /** Progresses animations by one frame. Parent objects should call on all child objects they render. */
  override def advanceFrame(): Unit = {
    if(drawOffsetX > 0) drawOffsetX = 0 max (drawOffsetX - Character.MOVE_SPEED)
    else if(drawOffsetX < 0) drawOffsetX = 0 min (drawOffsetX + Character.MOVE_SPEED)
    if(drawOffsetY > 0) drawOffsetY = 0 max (drawOffsetY - Character.MOVE_SPEED)
    else if(drawOffsetY < 0) drawOffsetY = 0 min (drawOffsetY + Character.MOVE_SPEED)
    if(!isMoving && queuedMove.nonEmpty){
      queuedMove.get.apply()
      System.out.println("Used queued move.")
      queuedMove = None
    }
  }
}
