package view.views

import java.awt.{Color, Graphics2D, Image}
import java.awt.image.BufferedImage
import java.io.File

import controller.{KeyMappings, SwitchViews}
import javax.imageio.ImageIO
import model.Model
import model.battle.{Battle, BattleInfoBox, BattleMessage, MoveSpecification}
import model.pokemon.Pokemon
import model.pokemon.move._
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

  /** Ends the battle and returns the player to the Overworld. */
  def endBattle(): Unit = {
    battle.endBattle()
    sendControllerMessage(SwitchViews(new OverworldView(model)))
  }

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
        menuActive = false
        processNextMoveEvent(battle.reorderMoveSpecifications(battle.makePlayerMove(move), battle.makeOpponentMove()),
          () => processNextMoveEvent(battle.getAfterMoveSpecifications.toList, () => Unit))
        menuActive = true
        showTrainerMenu()
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

  /** Attempts to learn the given move, then makes the callback. */
  protected def tryLearnMove(pokemon: Pokemon, move: Move, finalCallback: () => Unit): Unit =
    if(pokemon.getMoveList.isFull) ??? //TODO query user.
    else {
      pokemon.getMoveList.setNextAvailableMove(move)
      battleMessage = Some(createBattleMessage("%s learned %s!".format(pokemon.getName, move.getName), finalCallback))
    }


  /** Tries to level up the given pokemon, then makes the callback. */
  protected def tryLevelUpPokemon(pokemon: Pokemon, finalCallback: () => Unit): Unit =
    if (!pokemon.getLevelTracker.canLevelUp) finalCallback.apply()
    else {
      pokemon.getLevelTracker.levelUp()
      val nextCallback =
        if (pokemon.getMoveLearnedAtCurrentLevel.isEmpty) finalCallback
        else () => tryLearnMove(pokemon, pokemon.getMoveLearnedAtCurrentLevel.get, finalCallback)
      battleMessage = Some(createBattleMessage("%s grew to level %d!".format(pokemon.getName, pokemon.getLevel),
        nextCallback))
    }

  /** Runs the experience gain animation, then makes the callback. */
  protected def runExpGainAnimation(pokemon: Pokemon, amount: Int, finalCallback: () => Unit): Unit = {
    println("EXP animation")
    pokemon.getLevelTracker.gainExp(amount)
    finalCallback.apply()
  }


  /** Adds the given amount of experience to the pokemon, then makes the callback. */
  protected def gainExperience(pokemon: Pokemon, showMessage: Boolean, amount: Int, finalCallback: () => Unit): Unit = {
    val currentGainAmount = amount min pokemon.getLevelTracker.getExperienceToLevelUp
    val nextGainAmount = amount - currentGainAmount
    val nextCallback = if(nextGainAmount == 0) finalCallback else
      () => gainExperience(pokemon, false, nextGainAmount, finalCallback)
    if(showMessage) battleMessage = Some(createBattleMessage("%s gained %d experience.".format(pokemon.getName, amount),
      () => {
        runExpGainAnimation(pokemon, currentGainAmount, () => tryLevelUpPokemon(pokemon, nextCallback))
      }))
    else {
      runExpGainAnimation(pokemon, currentGainAmount, () => tryLevelUpPokemon(pokemon, nextCallback))
    }
  }

  //TODO the calculation would be better in Battle.
  /** Distributes experience from the opponent pokemon to any pokemon who have seen it, then calls the final
    * callback. */
  protected def distributeExperience(finalCallback: () => Unit): Unit = {
    val pokemonGainingExp = battle.getSeenOpponentPokemon.toList
    val totalExp = Pokemon.getExperienceAwarded(battle.getPlayerPokemon, battle.getOpponentPokemon,
      battle.isOpponentWild)
    val expForEach = totalExp / pokemonGainingExp.length
    //TODO don't know if this works for pokemon not in battle.
    def distributeNextExperience(pokemonList: List[Pokemon]): Unit =
      if(pokemonList.isEmpty) finalCallback.apply()
      else gainExperience(pokemonList.head, true, expForEach,
        () => distributeNextExperience(pokemonList.tail))
    distributeNextExperience(pokemonGainingExp)
  }

  /** Recursively processes one battle event at a time. This allows messages and animations to callback. */
  protected def processNextMoveEvent(events: List[MoveSpecification], finalCallback: () => Unit): Unit = {
    if(events.isEmpty) {
      finalCallback.apply()
      return
    }
    var recur_immediately = false
    events.head.moveEvent match {
      case DisplayMessage(message) =>
        battleMessage = Some(createBattleMessage(message, () => {
          battleMessage = None
          processNextMoveEvent(events.tail, finalCallback)
        }))
      case PlayAnimation(path) =>
        println("Animation at %s".format(path)) //TODO play animation.
        recur_immediately = true
      //case EndMove => return //TODO I'm not certain this is right.
      case FaintSelf =>
        if(events.head.movingPokemon == battle.getPlayerPokemon) ??? //TODO player pokemon faints.
        else distributeExperience(() => endBattle()) //TODO if this is a trainer battle, it's a little more complicated.
      case FaintOther =>
        if(events.head.movingPokemon == battle.getPlayerPokemon) distributeExperience(() => endBattle()) //TODO if this is a trainer battle, it's a little more complicated.
        else ??? //TODO player pokemon faints.
      case _ => recur_immediately = true
    }
    events.head.moveEvent.doEvent(events.head.movingPokemon, events.head.otherPokemon)
    if(recur_immediately) processNextMoveEvent(events.tail, finalCallback)
  }

  /** Attempts to run away from the opponent. */
  protected def tryRunAway(): Unit = {
    if(battleMessage.nonEmpty) throw new UnsupportedOperationException(
      "Cannot try runaway when battleMessage is nonEmpty.")
    //TODO runaway could fail.
    val callback = () => endBattle()
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
