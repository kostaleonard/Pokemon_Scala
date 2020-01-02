package controller

import java.awt.event.{KeyEvent, KeyListener}

import scala.collection.mutable.ListBuffer

class KeyPressManager(controller: Controller) extends KeyListener{
  protected val keyCodeMap: scala.collection.mutable.Map[Int, ListBuffer[Boolean]] = scala.collection.mutable.Map.empty[Int, ListBuffer[Boolean]]
  protected val keyMappings = new KeyMappings

  /** Returns the translated key code based on the user's key mappings. */
  def translateKeyCode(keyCode: Int): Int = keyMappings.getKeyMapping(keyCode)

  /** Called when a key is pressed. */
  override def keyPressed(e: KeyEvent): Unit = {
    val keyCode = translateKeyCode(e.getKeyCode)
    controller.keyPressed(keyCode)
  }

  /** Called when a key is released. */
  override def keyReleased(e: KeyEvent): Unit = {
    val keyCode = translateKeyCode(e.getKeyCode)
    controller.keyReleased(keyCode)
  }

  /** Called when a key is typed. */
  override def keyTyped(e: KeyEvent): Unit = {
    val keyCode = translateKeyCode(e.getKeyCode)
    controller.keyTyped(keyCode)
  }
}
