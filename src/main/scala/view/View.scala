package view

import controller.{ControllerMessage}
import model.Model

import scala.collection.mutable.ListBuffer

object View {
  val GBA_WIDTH = 240
  val GBA_HEIGHT = 160
  val FRAME_DESIGN_WIDTH: Int = GBA_WIDTH * 4
  val FRAME_DESIGN_HEIGHT: Int = GBA_HEIGHT * 4
  val IMAGE_DIRECTORY = "images"

  //TODO I am not certain that a relative path will work when this project changes hands. Works nicely with SBT, though.
  /** Returns the relative path to the given image. */
  def getSourcePath(imageFilename: String): String =
    "%s/%s/%s".format(Model.RESOURCE_ROOT_DIRECTORY, IMAGE_DIRECTORY, imageFilename)
}

abstract class View(protected val model: Model) extends Drawable {
  protected val controllerMessages: ListBuffer[ControllerMessage] = ListBuffer.empty
  protected var inputFrozen: Boolean = false

  /** Sends a message to the Controller. */
  def sendControllerMessage(message: ControllerMessage): Unit = controllerMessages.append(message)

  /** Clears controller messages. */
  def clearControllerMessages(): Unit = controllerMessages.clear

  /** Returns the controller messages as a List. */
  def getControllerMessages: List[ControllerMessage] = controllerMessages.toList

  /** Returns the object's width. */
  override def getObjectWidth: Int = View.FRAME_DESIGN_WIDTH

  /** Returns the object's height. */
  override def getObjectHeight: Int = View.FRAME_DESIGN_HEIGHT

  /** The action taken when a key is pressed and the View is in focus. */
  def keyPressed(keyCode: Int): Unit

  /** The action taken when a key is released and the View is in focus. */
  def keyReleased(keyCode: Int): Unit

  /** The action taken when a key is typed and the View is in focus. */
  def keyTyped(keyCode: Int): Unit

  /** The action taken when a key is held and the View is in focus. */
  def keyHeld(keyCode: Int): Unit
}
