package controller

import model.Model
import model.battle.Battle
import view.View
import view.frame.ViewManager
import view.views.{MainMenuView, OverworldView}

object Controller {
  val GAME_TITLE = "Gray"

  /** Runs the game. */
  def main(args: Array[String]): Unit = {
    val controller = new Controller
    controller.playGame()
  }
}

class Controller {
  protected val model: Model = Model.loadOrCreate(getProfileName)
  protected val viewManager = new ViewManager(new MainMenuView(model), this)

  /** Returns the player profile name. */
  def getProfileName: String = {
    //TODO get a profile name from the user (through the View--either saved or created).
    "Leo"
  }

  /** Starts the game. */
  def playGame(): Unit = runMainMenu()

  /** Runs the main menu. */
  def runMainMenu(): Unit = viewManager.setCurrentView(new MainMenuView(model))

  /** Clears the controller messages. */
  def clearControllerMessages(): Unit = viewManager.getCurrentView.clearControllerMessages()

  /** Checks the controller messages from the current view and acts on them. */
  def checkControllerMessages(): Unit = {
    val messages = viewManager.getCurrentView.getControllerMessages
    clearControllerMessages()
    messages.foreach {
      case SwitchViews(nextView) => changeViews(nextView)
      case SendKeyPress(keyCode) => keyPressed(keyCode)
      case m => throw new UnsupportedOperationException("Unrecognized Controller Message: " + m)
    }
  }

  /** Sets the current view to the new view. */
  def changeViews(nextView: View): Unit = viewManager.setCurrentView(nextView)

  /** Sends the key press to the current view, then checks for controller messages. */
  def keyPressed(keyCode: Int): Unit = {
    viewManager.getCurrentView.keyPressed(keyCode)
    checkControllerMessages()
  }

  /** Sends the key release to the current view, then checks for controller messages. */
  def keyReleased(keyCode: Int): Unit = {
    viewManager.getCurrentView.keyReleased(keyCode)
    checkControllerMessages()
  }

  /** Sends the key type to the current view, then checks for controller messages. */
  def keyTyped(keyCode: Int): Unit = {
    viewManager.getCurrentView.keyTyped(keyCode)
    checkControllerMessages()
  }

  /** Sends the key hold to the current view, then checks for controller messages. */
  def keyHeld(keyCode: Int): Unit = {
    viewManager.getCurrentView.keyHeld(keyCode)
    checkControllerMessages()
  }

  /** Exits the game by ending the program. */
  def exitGame(): Unit = {
    System.exit(0)
  }
}
