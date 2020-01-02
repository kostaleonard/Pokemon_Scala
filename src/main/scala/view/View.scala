package view

import controller.{ControllerMessage, KeyPressManager}

import scala.collection.mutable.ListBuffer

object View {
  val FRAME_DESIGN_WIDTH = 1600
  val FRAME_DESIGN_HEIGHT = 900
  val RESOURCE_ROOT_DIRECTORY = "resources"
  val IMAGE_DIRECTORY = "images"

  //TODO I am not certain that a relative path will work when this project changes hands. Works nicely with SBT, though.
  /** Returns the relative path to the given image. */
  def getSourcePath(imageFilename: String): String =
    "%s/%s/%s".format(RESOURCE_ROOT_DIRECTORY, IMAGE_DIRECTORY, imageFilename)
}

abstract class View(protected val keyPressManager: KeyPressManager) {
  protected val controllerMessages: ListBuffer[ControllerMessage] = ListBuffer.empty

  /** Returns the KeyPressManager. */
  def getKeyPressManager: KeyPressManager = keyPressManager

  /** Sends a message to the Controller. */
  def sendControllerMessage(message: ControllerMessage): Unit = controllerMessages.append(message)

  /** Clears controller messages. */
  def clearControllerMessages: Unit = controllerMessages.clear

  /** Returns the controller messages as a List. */
  def getControllerMessages: List[ControllerMessage] = controllerMessages.toList

  /** The action taken when a key is pressed and the View is in focus. */
  def keyPressed(keyCode: Int): Unit

  /** The action taken when a key is released and the View is in focus. */
  def keyReleased(keyCode: Int): Unit

  /** The action taken when a key is typed and the View is in focus. */
  def keyTyped(keyCode: Int): Unit

  /** The action taken when a key is held and the View is in focus. */
  def keyHeld(keyCode: Int): Unit
}
