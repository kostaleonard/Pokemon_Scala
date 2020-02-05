package view.views

import java.awt.{Color, Graphics2D, Image}
import java.awt.image.BufferedImage
import java.io.File

import controller.{KeyMappings, SwitchViews}
import javax.imageio.ImageIO
import model.Model
import model.battle.{Battle, BattleInfoBox, BattleMessage}
import model.pokemon.Pokemon
import model.pokemon.move.{DisplayMessage, EndMove, MoveEvent, PlayAnimation}
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
  val MULTIPAGE_TEST_STRING = "Hello, this is a test message from Leo. It is purposefully very long so that I can test the scrolling capability of my menu."
  val RUNAWAY_SUCCESSFUL_STRING = "Got away safely!"
  val BATTLE_MESSAGE_TRIANGLE_XPOINTS = Array(920, 950, 935)
  val BATTLE_MESSAGE_TRIANGLE_YPOINTS = Array(565, 565, 585)
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
  protected var currentMenu: BasicMenu = trainerMenu
  protected var menuActive: Boolean = true

  /** Sets up the trainer menu. */
  protected def setupTrainerMenu(): Unit = {
    trainerMenu.appendMenuItem(BasicMenuItem("FIGHT", GuiAction(() => showMoveMenu())))
    trainerMenu.appendMenuItem(BasicMenuItem("PKMN", GuiAction()))
    trainerMenu.appendMenuItem(BasicMenuItem("ITEM", GuiAction()))
    trainerMenu.appendMenuItem(BasicMenuItem("RUN", GuiAction(() => tryRunAway())))
    trainerMenu.setTitleDisplayed(false)
  }

  /** Sets up the move menu. */
  protected def setupMoveMenu(): Unit = {
    battle.getPlayerPokemon.getMoveList.getMoves.foreach(move =>
      moveMenu.appendMenuItem(BasicMenuItem(move.getName, GuiAction(() => {
        processEvents(battle.makePlayerMove(move), battle.getPlayerPokemon, battle.getOpponentPokemon)
        processEvents(battle.makeOpponentMove(), battle.getOpponentPokemon, battle.getPlayerPokemon)
      })))
    )
    moveMenu.setTitleDisplayed(false)
    moveMenu.setWrapContentHeight(false)
    moveMenu.setWidth(500)
    moveMenu.setHeight(BattleView.TRAINER_MENU_BOTTOM - BattleView.BATTLE_FOREGROUND_BOTTOM)
  }

  /** Switches the display to the move menu. */
  protected def showMoveMenu(): Unit = currentMenu = moveMenu

  /** Switches the display to the trainer menu. */
  protected def showTrainerMenu(): Unit = currentMenu = trainerMenu

  /** Processes the events so that the move or effects are complete. thisPokemon is the Pokemon using the move,
    * otherPokemon is the other. */
  protected def processEvents(events: Array[MoveEvent], thisPokemon: Pokemon, otherPokemon: Pokemon): Unit = {
    events.foreach { event => event match {
        case DisplayMessage(message) => System.out.println(message) //TODO show battle message
        case PlayAnimation(path) => System.out.println("Animation at %s".format(path)) //TODO play animation.
        case EndMove => return
        case _ => ;
      }
      event.doEvent(thisPokemon, otherPokemon)
    }
  }

  /** Attempts to run away from the opponent. */
  protected def tryRunAway(): Unit = {
    if(battleMessage.nonEmpty) throw new UnsupportedOperationException(
      "Cannot try runaway when battleMessage is nonEmpty.")
    //TODO runaway could fail.
    val callback = () => sendControllerMessage(SwitchViews(new OverworldView(model)))
    battleMessage = Some(createBattleMessage(BattleView.RUNAWAY_SUCCESSFUL_STRING, callback))
    menuActive = false
  }

  /** The action taken when a key is pressed and the View is in focus. */
  override def keyPressed(keyCode: Int): Unit = keyCode match{
    case KeyMappings.UP_KEY => if(menuActive) currentMenu.scrollUp()
    case KeyMappings.DOWN_KEY => if(menuActive) currentMenu.scrollDown()
    case KeyMappings.A_KEY =>
      if(battleMessage.nonEmpty && battleMessage.get.isOnLastPage(BattleView.BATTLE_MESSAGE_LINES_PER_PAGE))
        battleMessage.get.callbackOnMessageComplete.apply()
      else if(battleMessage.nonEmpty) advanceBattleMessage()
      else if(menuActive) currentMenu.makeSelection()
    case KeyMappings.B_KEY =>
      if(menuActive) showTrainerMenu()
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
    val nextLines =
      if(battleMessage.get.message.length <= 1) List.empty[String]
      else battleMessage.get.message.tail.tail
    battleMessage = Some(BattleMessage(nextLines, battleMessage.get.callbackOnMessageComplete))
  }

  /** Clears the battleMessage. */
  def clearBattleMessage(): Unit = {
    battleMessage = None
    menuActive = true
  }

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
    if(!battleMessage.get.isOnLastPage(BattleView.BATTLE_MESSAGE_LINES_PER_PAGE)){
      g2d.setColor(Color.RED.darker())
      g2d.fillPolygon(BattleView.BATTLE_MESSAGE_TRIANGLE_XPOINTS, BattleView.BATTLE_MESSAGE_TRIANGLE_YPOINTS, 3)
    }
  }

  /** Draws the trainer menu in the lower panel of the screen. */
  def drawTrainerMenu(g2d: Graphics2D): Unit = {
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
  }

  /** Draws the move menu in the lower panel of the screen. */
  def drawMoveMenu(g2d: Graphics2D): Unit = {
    g2d.setColor(Color.GRAY)
    g2d.fillRect(0, BattleView.BATTLE_FOREGROUND_BOTTOM, BattleView.BORDER_THICKNESS,
      BattleView.TRAINER_MENU_BOTTOM - BattleView.BATTLE_FOREGROUND_BOTTOM)
    g2d.fillRect(0, BattleView.BATTLE_FOREGROUND_BOTTOM, getObjectWidth, BattleView.BORDER_THICKNESS)
    g2d.fillRect(getObjectWidth - BattleView.BORDER_THICKNESS, BattleView.BATTLE_FOREGROUND_BOTTOM,
      BattleView.BORDER_THICKNESS, BattleView.TRAINER_MENU_BOTTOM - BattleView.BATTLE_FOREGROUND_BOTTOM)
    g2d.fillRect(0, BattleView.TRAINER_MENU_BOTTOM - BattleView.BORDER_THICKNESS, getObjectWidth,
      BattleView.BORDER_THICKNESS)
    g2d.drawImage(moveMenu.getImage, 0, BattleView.BATTLE_FOREGROUND_BOTTOM, null)
    val menuItemFont = BasicMenu.DEFAULT_MENUITEM_FONT
    g2d.setFont(menuItemFont)
    g2d.setColor(BasicMenu.DEFAULT_MENUITEM_FONT_COLOR)
    val menuItemHeight = g2d.getFontMetrics(menuItemFont).getHeight
    battle.getPlayerPokemon.getMoveList.getMoves.zipWithIndex.foreach { tup =>
      val move = tup._1
      val i = tup._2
      val heightStartThisMenuItem = menuItemHeight * i
      g2d.drawString("%d/%d".format(move.getCurrentPP, move.getMaxPP), 400,
        BattleView.BATTLE_FOREGROUND_BOTTOM + heightStartThisMenuItem + (menuItemHeight * 3) / 4)
    }
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
    if(battleMessage.nonEmpty) drawBattleMessage(g2d)
    else if(menuActive && currentMenu == trainerMenu) drawTrainerMenu(g2d)
    else if(menuActive && currentMenu == moveMenu) drawMoveMenu(g2d)
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
