package view.views

import controller.KeyPressManager
import view.View

class BattleView(override protected val keyPressManager: KeyPressManager) extends View(keyPressManager) {
  /** The action taken when a key is pressed and the View is in focus. */
  override def keyPressed(keyCode: Int): Unit = ???

  /** The action taken when a key is released and the View is in focus. */
  override def keyReleased(keyCode: Int): Unit = ???

  /** The action taken when a key is typed and the View is in focus. */
  override def keyTyped(keyCode: Int): Unit = ???

  /** The action taken when a key is held and the View is in focus. */
  override def keyHeld(keyCode: Int): Unit = ???
}
