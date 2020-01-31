package view.views

import java.awt.{Color, Graphics2D, Image}
import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO
import model.Model
import model.battle.Battle
import view.View
import view.gui.GuiAction
import view.gui.menu.{BasicMenu, BasicMenuItem}

object BattleView {
  val BACKGROUND_IMAGE: Image = ImageIO.read(
    new File(View.getSourcePath("backgrounds/background_grassy_default.png")))
  val BACKGROUND_IMAGE_SCALE_FACTOR = 4
}

class BattleView(override protected val model: Model, battle: Battle) extends View(model) {
  protected var canvasImage: BufferedImage = new BufferedImage(getObjectWidth, getObjectHeight,
    BufferedImage.TYPE_INT_ARGB)
  val prescaledBackground: BufferedImage = getPrescaledImage.get
  val trainerMenu: BasicMenu = new BasicMenu
  val moveMenu: BasicMenu = new BasicMenu
  setupTrainerMenu()
  setupMoveMenu()

  /** Sets up the trainer menu. */
  protected def setupTrainerMenu(): Unit = {
    trainerMenu.appendMenuItem(BasicMenuItem("FIGHT", GuiAction()))
    trainerMenu.appendMenuItem(BasicMenuItem("PKMN", GuiAction()))
    trainerMenu.appendMenuItem(BasicMenuItem("ITEM", GuiAction()))
    trainerMenu.appendMenuItem(BasicMenuItem("RUN", GuiAction()))
  }

  /** Sets up the move menu. */
  protected def setupMoveMenu(): Unit = {
    //TODO set up the move menu.
  }

  /** The action taken when a key is pressed and the View is in focus. */
  override def keyPressed(keyCode: Int): Unit = ???

  /** The action taken when a key is released and the View is in focus. */
  override def keyReleased(keyCode: Int): Unit = ???

  /** The action taken when a key is typed and the View is in focus. */
  override def keyTyped(keyCode: Int): Unit = ???

  /** The action taken when a key is held and the View is in focus. */
  override def keyHeld(keyCode: Int): Unit = ???

  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  override def getImage: BufferedImage = {
    val g2d = canvasImage.getGraphics.asInstanceOf[Graphics2D]
    g2d.drawImage(prescaledBackground, 0, 0, null)
    val opponentPokemonImage = battle.getOpponentPokemon.getPrescaledImageFront.get
    val playerPokemonImage = battle.getPlayerPokemon.getPrescaledImageBack.get
    g2d.drawImage(opponentPokemonImage, 575, 85, null)
    g2d.drawImage(playerPokemonImage, 50, 192, null)
    g2d.drawImage(trainerMenu.getImage, 600, 300, null)
    g2d.dispose()
    canvasImage
  }

  /** Returns the object's image, already scaled. This is to speed up rendering. */
  override def getPrescaledImage: Option[BufferedImage] = {
    val bufferedImage = new BufferedImage(getObjectWidth, getObjectHeight, BufferedImage.TYPE_INT_ARGB)
    val g2d = bufferedImage.getGraphics.asInstanceOf[Graphics2D]
    val width = BattleView.BACKGROUND_IMAGE.getWidth(null) * BattleView.BACKGROUND_IMAGE_SCALE_FACTOR
    val height = BattleView.BACKGROUND_IMAGE.getHeight(null) * BattleView.BACKGROUND_IMAGE_SCALE_FACTOR
    g2d.drawImage(BattleView.BACKGROUND_IMAGE, 0, 0, width, height, null)
    g2d.dispose()
    Some(bufferedImage)
  }

  /** Progresses animations by one frame. Parent objects should call on all child objects they render. */
  override def advanceFrame(): Unit = {}
}
