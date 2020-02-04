package view.views

import java.awt.{Color, Graphics2D, Image}
import java.awt.image.BufferedImage
import java.io.File

import controller.{KeyMappings, SwitchViews}
import javax.imageio.ImageIO
import model.Model
import model.battle.{Battle, BattleInfoBox, BattleMessage}
import view.View
import view.gui.GuiAction
import view.gui.menu.{BasicMenu, BasicMenuItem}

import scala.collection.mutable.ListBuffer

object BattleView {
  val BACKGROUND_IMAGE: Image = ImageIO.read(
    new File(View.getSourcePath("backgrounds/background_grassy_default.png")))
  val BACKGROUND_IMAGE_SCALE_FACTOR = 4
  val BATTLE_FOREGROUND_BOTTOM = 448
  val TRAINER_MENU_BOTTOM = 592
  val BORDER_THICKNESS = 4
  val BATTLE_MESSAGE_LINES_PER_PAGE = 2
  val BATTLE_MESSAGE_LINE_SIZE = 29
  val RUNAWAY_SUCCESSFUL_STRING = "Got away safely!"
}

class BattleView(override protected val model: Model, battle: Battle) extends View(model) {
  protected var canvasImage: BufferedImage = new BufferedImage(getObjectWidth, getObjectHeight,
    BufferedImage.TYPE_INT_ARGB)
  protected val prescaledBackground: BufferedImage = getPrescaledImage.get
  protected val playerPokemonInfoBox: BattleInfoBox = new BattleInfoBox(battle.getPlayerPokemon, true)
  protected val opponentPokemonInfoBox: BattleInfoBox = new BattleInfoBox(
    battle.getOpponentPokemon, false)
  protected var battleMessage: Option[BattleMessage] = None
  protected val trainerMenu: BasicMenu = new BasicMenu
  protected val moveMenu: BasicMenu = new BasicMenu
  setupTrainerMenu()
  setupMoveMenu()

  /** Sets up the trainer menu. */
  protected def setupTrainerMenu(): Unit = {
    trainerMenu.appendMenuItem(BasicMenuItem("FIGHT", GuiAction()))
    trainerMenu.appendMenuItem(BasicMenuItem("PKMN", GuiAction()))
    trainerMenu.appendMenuItem(BasicMenuItem("ITEM", GuiAction()))
    trainerMenu.appendMenuItem(BasicMenuItem("RUN", GuiAction(() => tryRunAway())))
    trainerMenu.setTitleDisplayed(false)
  }

  /** Sets up the move menu. */
  protected def setupMoveMenu(): Unit = {
    //TODO set up the move menu.
  }

  /** Attempts to run away from the opponent. */
  protected def tryRunAway(): Unit = {
    if(battleMessage.nonEmpty) throw new UnsupportedOperationException(
      "Cannot try runaway when battleMessage is nonEmpty.")
    //TODO runaway could fail.
    val callback = () => sendControllerMessage(SwitchViews(new OverworldView(model)))
    battleMessage = Some(createBattleMessage(BattleView.RUNAWAY_SUCCESSFUL_STRING, callback))
  }

  /** The action taken when a key is pressed and the View is in focus. */
  override def keyPressed(keyCode: Int): Unit = keyCode match{
    case KeyMappings.UP_KEY => trainerMenu.scrollUp()
    case KeyMappings.DOWN_KEY => trainerMenu.scrollDown()
    case KeyMappings.A_KEY =>
      if(battleMessage.isEmpty) trainerMenu.makeSelection() //TODO advance battleMessage. If the battleMessage is not empty, but its message IS, then this will instead execute the callback.
      else if(battleMessage.get.isOnLastPage(BattleView.BATTLE_MESSAGE_LINES_PER_PAGE))
        battleMessage.get.callbackOnMessageComplete.apply()
      else advanceBattleMessage()
    case _ => ;
  }

  /** The action taken when a key is released and the View is in focus. */
  override def keyReleased(keyCode: Int): Unit = {}

  /** The action taken when a key is typed and the View is in focus. */
  override def keyTyped(keyCode: Int): Unit = {}

  /** The action taken when a key is held and the View is in focus. */
  override def keyHeld(keyCode: Int): Unit = {}

  /** Fills battleMessage with the given message, split based on the display size of the lower panel. This will be
    * shown to the user in chunks. */
  def createBattleMessage(message: String, callback: () => Unit): BattleMessage = {
    val resultLines = new ListBuffer[String]
    var currentLine = ""
    var remainingWords = message.split(" ")
    while(remainingWords.nonEmpty){
      val nextWord = if(currentLine.isEmpty) remainingWords.head else " " + remainingWords.head
      if(currentLine.length + nextWord.length > BattleView.BATTLE_MESSAGE_LINE_SIZE) {
        resultLines.append(currentLine)
        currentLine = nextWord.stripPrefix(" ")
      }
      else currentLine += nextWord
      remainingWords = remainingWords.tail
    }
    if(currentLine.nonEmpty) resultLines.append(currentLine)
    BattleMessage(resultLines.toList, callback)
  }

  /** Sets the displayed message to the next lines. */
  def advanceBattleMessage(): Unit = {
    if(battleMessage.isEmpty) throw new UnsupportedOperationException("Cannot advance message when message is empty.")
    val nextLines = if(battleMessage.get.message.length <= 1) List.empty[String] else battleMessage.get.message.tail.tail
    battleMessage = Some(BattleMessage(nextLines, battleMessage.get.callbackOnMessageComplete))
  }

  /** Clears the battleMessage. */
  def clearBattleMessage(): Unit = battleMessage = None

  /** Draws the battle message in the lower panel of the screen. */
  def drawBattleMessage(g2d: Graphics2D): Unit = {
    if(battleMessage.isEmpty) throw new UnsupportedOperationException("Cannot draw empty battle message.")
    g2d.setColor(BasicMenu.DEFAULT_MENU_BACKGROUND_COLOR)
    g2d.fillRect(0, BattleView.BATTLE_FOREGROUND_BOTTOM, getObjectWidth,
      getObjectHeight - BattleView.BATTLE_FOREGROUND_BOTTOM)
    g2d.setColor(Color.BLACK)
    g2d.fillRect(0, BattleView.TRAINER_MENU_BOTTOM, getObjectWidth, getObjectHeight - BattleView.TRAINER_MENU_BOTTOM)
    g2d.setColor(Color.GRAY)
    g2d.fillRect(0, BattleView.BATTLE_FOREGROUND_BOTTOM, BattleView.BORDER_THICKNESS,
      BattleView.TRAINER_MENU_BOTTOM - BattleView.BATTLE_FOREGROUND_BOTTOM)
    g2d.fillRect(0, BattleView.BATTLE_FOREGROUND_BOTTOM, getObjectWidth, BattleView.BORDER_THICKNESS)
    g2d.fillRect(getObjectWidth - BattleView.BORDER_THICKNESS, BattleView.BATTLE_FOREGROUND_BOTTOM,
      BattleView.BORDER_THICKNESS, BattleView.TRAINER_MENU_BOTTOM - BattleView.BATTLE_FOREGROUND_BOTTOM)
    g2d.fillRect(0, BattleView.TRAINER_MENU_BOTTOM - BattleView.BORDER_THICKNESS, getObjectWidth,
      BattleView.BORDER_THICKNESS)
    g2d.setFont(BasicMenu.DEFAULT_TITLE_FONT)
    g2d.setColor(Color.BLACK)
    if(battleMessage.get.message.nonEmpty) g2d.drawString(battleMessage.get.message.head,
      BattleView.BORDER_THICKNESS * 2, BattleView.BATTLE_FOREGROUND_BOTTOM + 60)
    if(battleMessage.get.message.length > 1) g2d.drawString(battleMessage.get.message.tail.head,
      BattleView.BORDER_THICKNESS * 2, BattleView.BATTLE_FOREGROUND_BOTTOM + 120)
    //TODO if more of the message, draw the little arrowhead.
  }

  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  override def getImage: BufferedImage = {
    val g2d = canvasImage.getGraphics.asInstanceOf[Graphics2D]
    g2d.drawImage(prescaledBackground, 0, 0, null)
    val opponentPokemonImage = battle.getOpponentPokemon.getPrescaledImageFront.get
    val playerPokemonImage = battle.getPlayerPokemon.getPrescaledImageBack.get
    g2d.drawImage(opponentPokemonImage, 575, 85, null)
    g2d.drawImage(playerPokemonImage, 50, 192, null)
    g2d.drawImage(playerPokemonInfoBox.getImage, BattleInfoBox.SINGLE_PLAYER_OFFSET_X,
      BattleInfoBox.SINGLE_PLAYER_OFFSET_Y, null)
    g2d.drawImage(opponentPokemonInfoBox.getImage, BattleInfoBox.SINGLE_OPPONENT_OFFSET_X,
      BattleInfoBox.SINGLE_OPPONENT_OFFSET_Y, null)
    g2d.setColor(BasicMenu.DEFAULT_MENU_BACKGROUND_COLOR)
    g2d.fillRect(0, BattleView.BATTLE_FOREGROUND_BOTTOM, getObjectWidth,
      getObjectHeight - BattleView.BATTLE_FOREGROUND_BOTTOM)
    g2d.setColor(Color.BLACK)
    g2d.fillRect(0, BattleView.TRAINER_MENU_BOTTOM, getObjectWidth, getObjectHeight - BattleView.TRAINER_MENU_BOTTOM)
    g2d.setColor(Color.GRAY)
    g2d.fillRect(0, BattleView.BATTLE_FOREGROUND_BOTTOM, BattleView.BORDER_THICKNESS,
      BattleView.TRAINER_MENU_BOTTOM - BattleView.BATTLE_FOREGROUND_BOTTOM)
    g2d.fillRect(0, BattleView.BATTLE_FOREGROUND_BOTTOM, getObjectWidth, BattleView.BORDER_THICKNESS)
    g2d.fillRect(getObjectWidth - BattleView.BORDER_THICKNESS, BattleView.BATTLE_FOREGROUND_BOTTOM,
      BattleView.BORDER_THICKNESS, BattleView.TRAINER_MENU_BOTTOM - BattleView.BATTLE_FOREGROUND_BOTTOM)
    g2d.fillRect(0, BattleView.TRAINER_MENU_BOTTOM - BattleView.BORDER_THICKNESS, getObjectWidth,
      BattleView.BORDER_THICKNESS)
    g2d.drawImage(trainerMenu.getImage, 660, BattleView.BATTLE_FOREGROUND_BOTTOM, null)
    g2d.setFont(BasicMenu.DEFAULT_TITLE_FONT)
    g2d.setColor(Color.BLACK)
    g2d.drawString("What will", BattleView.BORDER_THICKNESS * 2, BattleView.BATTLE_FOREGROUND_BOTTOM + 60)
    g2d.drawString("%s do?".format(battle.getPlayerPokemon.getName), BattleView.BORDER_THICKNESS * 2,
      BattleView.BATTLE_FOREGROUND_BOTTOM + 120)
    if(battleMessage.nonEmpty) drawBattleMessage(g2d)
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
