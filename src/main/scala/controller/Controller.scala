package controller

abstract class Controller {
  //TODO abstract, PrintController should extend.
  //TODO I'm actually not entirely sure the Controller should follow the abstract/concrete pattern.
  //TODO take a look at Mass--I really had a good design pattern there.

  /** Called when a key is pressed. */
  def keyPressed(keyCode: Int): Unit

  /** Called when a key is released. */
  def keyReleased(keyCode: Int): Unit

  /** Called when a key is typed. */
  def keyTyped(keyCode: Int): Unit
}
