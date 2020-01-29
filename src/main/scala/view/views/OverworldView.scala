package view.views

import java.awt.{Color, Graphics2D, Point}
import java.awt.image.BufferedImage

import controller.{KeyMappings, SwitchViews}
import model.Model
import model.battle.Battle
import model.board._
import view.View

import scala.util.Random

object OverworldView {
  val RANDOM_ENCOUNTER_ANIMATION_FRAMES = 120
}

class OverworldView(override protected val model: Model) extends View(model) {
  protected var randomEncounterAnimationStarted: Boolean = false
  protected var randomEncounterAnimationFrame: Int = 0

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
        playerCharacter.queueFunctionAfterAnimation(
          Some(() => queueRandomEncounter(battle)))
      }
    }
  }

  /** Starts the random encounter animation, and queues the view switch after this animation. */
  def queueRandomEncounter(battle: Battle): Unit = {
    inputFrozen = true
    randomEncounterAnimationStarted = true
    queueFunctionAfterAnimation(Some(() => sendControllerMessage(SwitchViews(new BattleView(model, battle)))))
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
    if(randomEncounterAnimationStarted){
      g2d.fillRect(0, 0, randomEncounterAnimationFrame, getObjectHeight)
      g2d.fillRect(getObjectWidth - randomEncounterAnimationFrame, 0, randomEncounterAnimationFrame, getObjectHeight)
    }
    g2d.dispose()
    canvasImage
  }

  /** Returns the object's image, already scaled. This is to speed up rendering. */
  override def getPrescaledImage: Option[BufferedImage] = None

  /** Progresses animations by one frame. Parent objects should call on all child objects they render. */
  override def advanceFrame(): Unit = {
    model.getCurrentBoard.advanceFrame()
    if(randomEncounterAnimationStarted) {
      randomEncounterAnimationFrame += 1
      if (randomEncounterAnimationFrame == OverworldView.RANDOM_ENCOUNTER_ANIMATION_FRAMES)
        executeAfterAnimation.getOrElse(
          throw new UnsupportedOperationException("Expected random encounter code, but found nothing.")
        ).apply()
    }
  }
}
