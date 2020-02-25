package view.views

import java.awt.{Color, Graphics2D, Point}
import java.awt.image.BufferedImage

import controller.{KeyMappings, SwitchViews}
import model.Model
import model.battle.Battle
import model.board._
import view.View
import view.views.drawing.Animation
import view.views.drawing.animations.RandomEncounterAnimation1

import scala.util.Random

object OverworldView {
  val RANDOM_ENCOUNTER_ANIMATION_FRAMES = 180
}

class OverworldView(override protected val model: Model) extends View(model) {
  protected var randomEncounterAnimation: Option[Animation] = None
  protected var canvasImage: BufferedImage = new BufferedImage(getObjectWidth, getObjectHeight,
    BufferedImage.TYPE_INT_ARGB)

  //TODO randomly choose next random encounter animation.
  /** Returns the animation that will play for the next random encounter. */
  def getNextRandomEncounterAnimation(battle: Battle): Animation = new RandomEncounterAnimation1(
    Some(() => sendControllerMessage(SwitchViews(new BattleView(model, battle)))))

  /** Returns the offset for centering the player. */
  def getCenteringOffset: Point = {
    val loc = model.getPlayerLocation.get
    val playerCharacter = model.getPlayerCharacter
    new Point(
      ((loc.col + 0.5) * Board.TILE_SIZE).toInt - View.FRAME_DESIGN_WIDTH / 2 - playerCharacter.getDrawOffsetX,
      ((loc.row + 0.5) * Board.TILE_SIZE).toInt - View.FRAME_DESIGN_HEIGHT / 2 - playerCharacter.getDrawOffsetY
    )
  }

  /** Tries to start a random encounter from the cell on which the player is standing. */
  protected def tryStartRandomEncounter(): Unit = {
    val playerCharacter = model.getPlayerCharacter
    val newPlayerLoc = model.getPlayerLocation
    if(newPlayerLoc.nonEmpty){
      val cell = model.getCurrentBoard.getCells.apply(newPlayerLoc.get.row)(newPlayerLoc.get.col)
      if(Random.nextDouble() < cell.getRandomEncounterChance){
        val battle = new Battle(playerCharacter, None, Some(cell.getRandomWildPokemon))
        playerCharacter.setAnimationCallback(
          Some(() => {
            queueRandomEncounter(battle)
            playerCharacter.setAnimationCallback(None)
          }))
      }
    }
  }

  /** Starts the random encounter animation, and queues the view switch after this animation. */
  def queueRandomEncounter(battle: Battle): Unit = {
    inputFrozen = true
    randomEncounterAnimation = Some(getNextRandomEncounterAnimation(battle))
    randomEncounterAnimation.get.start()
  }

  //TODO clean this up.
  /** The action taken when a key is pressed and the View is in focus. */
  override def keyPressed(keyCode: Int): Unit = if(!inputFrozen) {
    val playerCharacter = model.getPlayerCharacter
    val playerLoc = model.getPlayerLocation
    keyCode match {
      case KeyMappings.DOWN_KEY =>
        if (!playerCharacter.isMoving) {
          playerLoc.map(_ => model.sendPlayerInDirection(South))
          tryStartRandomEncounter()
        }
        else if (playerCharacter.isAlmostDoneMoving) playerCharacter.queueMove(
          () => playerLoc.map(_ => model.sendPlayerInDirection(South)))
      case KeyMappings.UP_KEY =>
        if (!playerCharacter.isMoving){
          playerLoc.map(_ => model.sendPlayerInDirection(North))
          tryStartRandomEncounter()
        }
        else if (playerCharacter.isAlmostDoneMoving) playerCharacter.queueMove(
          () => playerLoc.map(_ => model.sendPlayerInDirection(North)))
      case KeyMappings.LEFT_KEY =>
        if (!playerCharacter.isMoving){
          playerLoc.map(_ => model.sendPlayerInDirection(West))
          tryStartRandomEncounter()
        }
        else if (playerCharacter.isAlmostDoneMoving) playerCharacter.queueMove(
          () => playerLoc.map(_ => model.sendPlayerInDirection(West)))
      case KeyMappings.RIGHT_KEY =>
        if (!playerCharacter.isMoving){
          playerLoc.map(_ => model.sendPlayerInDirection(East))
          tryStartRandomEncounter()
        }
        else if (playerCharacter.isAlmostDoneMoving) playerCharacter.queueMove(
          () => playerLoc.map(_ => model.sendPlayerInDirection(East)))
      case _ => ;
    }
  }

  /** The action taken when a key is released and the View is in focus. */
  override def keyReleased(keyCode: Int): Unit = {}

  /** The action taken when a key is typed and the View is in focus. */
  override def keyTyped(keyCode: Int): Unit = {}

  /** The action taken when a key is held and the View is in focus. */
  override def keyHeld(keyCode: Int): Unit = keyPressed(keyCode)

  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  override def getImage: BufferedImage = {
    val g2d = canvasImage.getGraphics.asInstanceOf[Graphics2D]
    val boardImage = model.getCurrentBoard.getImage
    val centeringOffset = getCenteringOffset
    g2d.setColor(Color.BLACK)
    g2d.fillRect(0, 0, getObjectWidth, getObjectHeight)
    g2d.drawImage(boardImage, -centeringOffset.x, -centeringOffset.y, null)
    if(randomEncounterAnimation.nonEmpty)
      g2d.drawImage(randomEncounterAnimation.get.getPrescaledImage.get, 0, 0, null)
    g2d.dispose()
    canvasImage
  }

  /** Returns the object's image, already scaled. This is to speed up rendering. */
  override def getPrescaledImage: Option[BufferedImage] = None

  /** Progresses animations by one frame. Parent objects should call on all child objects they render. */
  override def advanceFrame(): Unit = {
    model.getCurrentBoard.advanceFrame()
    if(randomEncounterAnimation.nonEmpty){
      randomEncounterAnimation.get.advanceFrame()
      if(randomEncounterAnimation.get.isAnimationComplete) randomEncounterAnimation.get.makeAnimationCallback()
    }
  }
}
