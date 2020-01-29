package model.actor

import java.awt.{Color, Graphics2D, Image}
import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO
import model.board._
import view.View

object Actor {
  val MOVE_SPEED: Int = Board.TILE_SIZE / 16
  val RUN_SPEED: Int = Board.TILE_SIZE / 8
  val ALMOST_DONE_MOVING_MULTIPLIER = 2

  val PLAYER_SOUTH = 10
  val PLAYER_SOUTH_WALK_LEFT = 11
  val PLAYER_SOUTH_WALK_RIGHT = 12
  val PLAYER_SOUTH_RUN_LEFT = 13
  val PLAYER_SOUTH_RUN_RIGHT = 14

  val PLAYER_EAST = 20
  val PLAYER_EAST_WALK_LEFT = 21
  val PLAYER_EAST_WALK_RIGHT = 22
  val PLAYER_EAST_RUN_LEFT = 23
  val PLAYER_EAST_RUN_RIGHT = 24

  val PLAYER_NORTH = 30
  val PLAYER_NORTH_WALK_LEFT = 31
  val PLAYER_NORTH_WALK_RIGHT = 32
  val PLAYER_NORTH_RUN_LEFT = 33
  val PLAYER_NORTH_RUN_RIGHT = 34

  val PLAYER_WEST = 40
  val PLAYER_WEST_WALK_LEFT = 41
  val PLAYER_WEST_WALK_RIGHT = 42
  val PLAYER_WEST_RUN_LEFT = 43
  val PLAYER_WEST_RUN_RIGHT = 44

  //TODO get the actual player avatar.
  /** Returns the avatar used for the player character. */
  def getPlayerAvatar: Image =
    ImageIO.read(new File(View.getSourcePath("sprites/player/player_south.png")))

  //TODO converting this to an Array will speed up access time. But that's also less clean, I feel.
  //TODO is it going to cause problems, putting all this in memory?
  /** The map of all frame numbers to their avatars. These are all of the images needed to render the player. */
  val playerAvatarMap: scala.collection.immutable.Map[Int, Image] = scala.collection.immutable.Map(
    PLAYER_SOUTH -> ImageIO.read(new File(View.getSourcePath("sprites/player/player_south.png"))),
    PLAYER_SOUTH_WALK_LEFT -> ImageIO.read(new File(View.getSourcePath("sprites/player/player_south_walk_left.png"))),
    PLAYER_SOUTH_WALK_RIGHT -> ImageIO.read(new File(View.getSourcePath("sprites/player/player_south_walk_right.png"))),
    PLAYER_EAST -> ImageIO.read(new File(View.getSourcePath("sprites/player/player_east.png"))),
    PLAYER_EAST_WALK_LEFT -> ImageIO.read(new File(View.getSourcePath("sprites/player/player_east_walk_left.png"))),
    PLAYER_EAST_WALK_RIGHT -> ImageIO.read(new File(View.getSourcePath("sprites/player/player_east_walk_right.png"))),
    PLAYER_NORTH -> ImageIO.read(new File(View.getSourcePath("sprites/player/player_north.png"))),
    PLAYER_NORTH_WALK_LEFT -> ImageIO.read(new File(View.getSourcePath("sprites/player/player_north_walk_left.png"))),
    PLAYER_NORTH_WALK_RIGHT -> ImageIO.read(new File(View.getSourcePath("sprites/player/player_north_walk_right.png"))),
    PLAYER_WEST -> ImageIO.read(new File(View.getSourcePath("sprites/player/player_west.png"))),
    PLAYER_WEST_WALK_LEFT -> ImageIO.read(new File(View.getSourcePath("sprites/player/player_west_walk_left.png"))),
    PLAYER_WEST_WALK_RIGHT -> ImageIO.read(new File(View.getSourcePath("sprites/player/player_west_walk_right.png")))
  )
}

class Actor extends BoardObject {
  //TODO you're going to have to end up defining classes of avatars to handle animations, then make it a constructor argument.
  protected var avatarFrame: Int = Actor.PLAYER_SOUTH
  protected var queuedMove: Option[() => Unit] = None
  protected var facingDirection: Direction = South
  protected var onLeftFoot: Boolean = true

  /** Returns the player's current avatar. */
  def getAvatar: Image = Actor.playerAvatarMap(avatarFrame)

  /** Turns the player's avatar. */
  protected def turnAvatar(direction: Direction): Unit = direction match {
    case South => avatarFrame = Actor.PLAYER_SOUTH
    case East => avatarFrame = Actor.PLAYER_EAST
    case North => avatarFrame = Actor.PLAYER_NORTH
    case West => avatarFrame = Actor.PLAYER_WEST
  }

  /** Sets the player's avatar to walking. */
  protected def setStandingAvatar(): Unit = facingDirection match {
    case South => avatarFrame = Actor.PLAYER_SOUTH
    case East => avatarFrame = Actor.PLAYER_EAST
    case North => avatarFrame = Actor.PLAYER_NORTH
    case West => avatarFrame = Actor.PLAYER_WEST
  }

  /** Sets the player's avatar to walking. */
  protected def setWalkingAvatar(): Unit = facingDirection match {
    case South => avatarFrame = if(onLeftFoot) Actor.PLAYER_SOUTH_WALK_LEFT else Actor.PLAYER_SOUTH_WALK_RIGHT
    case East => avatarFrame = if(onLeftFoot) Actor.PLAYER_EAST_WALK_LEFT else Actor.PLAYER_EAST_WALK_RIGHT
    case North => avatarFrame = if(onLeftFoot) Actor.PLAYER_NORTH_WALK_LEFT else Actor.PLAYER_NORTH_WALK_RIGHT
    case West => avatarFrame = if(onLeftFoot) Actor.PLAYER_WEST_WALK_LEFT else Actor.PLAYER_WEST_WALK_RIGHT
  }

  /** Returns the direction the player is facing. */
  def getFacingDirection: Direction = facingDirection

  /** Changes the direction the player is facing. */
  def setFacingDirection(direction: Direction): Unit = {
    facingDirection = direction
    turnAvatar(direction)
  }

  /** Switches the player's step. */
  def alternateStep(): Unit = onLeftFoot = !onLeftFoot

  /** Returns true if the character is moving. */
  def isMoving: Boolean = drawOffsetX != 0 || drawOffsetY != 0

  /** Returns true if the character is almost done moving so that the next move can be scheduled. This is just for
    * player quality of life. */
  def isAlmostDoneMoving: Boolean =
    math.abs(drawOffsetX) < Actor.ALMOST_DONE_MOVING_MULTIPLIER * Actor.MOVE_SPEED &&
    math.abs(drawOffsetY) < Actor.ALMOST_DONE_MOVING_MULTIPLIER * Actor.MOVE_SPEED

  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  override def getImage: BufferedImage = {
    canvasImage = new BufferedImage(getObjectWidth, getObjectHeight, BufferedImage.TYPE_INT_ARGB)
    val g2d = canvasImage.getGraphics.asInstanceOf[Graphics2D]
    g2d.drawImage(getAvatar, 0, 0, (getObjectWidth * 0.8).toInt, getObjectHeight, null)
    g2d.dispose()
    canvasImage
  }

  //TODO prescaled images for actors.
  /** Returns the object's image, already scaled. This is to speed up rendering. */
  override def getPrescaledImage: Option[BufferedImage] = None

  /** Queues the given move. The move function will be called when the player finishes the current move. */
  def queueMove(moveFunction: () => Unit): Unit = queuedMove = Some(moveFunction)

  /** Progresses animations by one frame. Parent objects should call on all child objects they render. */
  override def advanceFrame(): Unit = {
    if(drawOffsetX > 0) drawOffsetX = 0 max (drawOffsetX - Actor.MOVE_SPEED)
    else if(drawOffsetX < 0) drawOffsetX = 0 min (drawOffsetX + Actor.MOVE_SPEED)
    if(drawOffsetY > 0) drawOffsetY = 0 max (drawOffsetY - Actor.MOVE_SPEED)
    else if(drawOffsetY < 0) drawOffsetY = 0 min (drawOffsetY + Actor.MOVE_SPEED)
    if(isMoving && !isAlmostDoneMoving) setWalkingAvatar()
    else setStandingAvatar()
    if(!isMoving && queuedMove.nonEmpty){
      queuedMove.get.apply()
      queuedMove = None
    }
  }
}
