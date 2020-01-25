package controller

import java.awt.event.{ActionEvent, ActionListener, KeyEvent, KeyListener}

import javax.swing.Timer
import view.frame.ViewManager

import scala.collection.mutable.ListBuffer

object KeyPressManager {
  val KEYHELD_EVENTS_PER_SECOND = 60
}

class KeyPressManager(controller: Controller) extends KeyListener {
  protected val keyCodeMap: scala.collection.mutable.Map[Int, ListBuffer[Boolean]] = scala.collection.mutable.Map.empty[Int, ListBuffer[Boolean]]
  protected val keyMappings = new KeyMappings
  protected val heldKeys: scala.collection.mutable.Set[Int] = scala.collection.mutable.Set.empty[Int]
  protected val keyTimer = new Timer(ViewManager.MILLISECONDS_PER_SECOND / KeyPressManager.KEYHELD_EVENTS_PER_SECOND,
    new KeyHeldListener)
  keyTimer.start()

  /** Returns the key timer. */
  def getKeyTimer: Timer = keyTimer

  /** Returns the translated key code based on the user's key mappings. */
  def translateKeyCode(keyCode: Int): Int = keyMappings.getKeyMapping(keyCode)

  /** Called when a key is pressed. */
  override def keyPressed(e: KeyEvent): Unit = {
    val keyCode = translateKeyCode(e.getKeyCode)
    if(!heldKeys(keyCode)) {
      heldKeys.add(keyCode)
      controller.keyPressed(keyCode)
    }
  }

  /** Called when a key is released. */
  override def keyReleased(e: KeyEvent): Unit = {
    val keyCode = translateKeyCode(e.getKeyCode)
    heldKeys.remove(keyCode)
    controller.keyReleased(keyCode)
  }

  /** Called when a key is typed. */
  override def keyTyped(e: KeyEvent): Unit = {
    val keyCode = translateKeyCode(e.getKeyCode)
    controller.keyTyped(keyCode)
  }

  class KeyHeldListener extends ActionListener {
    /** Called every time the timer expires. */
    override def actionPerformed(e: ActionEvent): Unit = {
      heldKeys.foreach(controller.keyHeld)
    }
  }
}
