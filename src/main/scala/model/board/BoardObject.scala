package model.board

import view.views.drawing.{FrameByFrameAnimation}

/** Represents a dynamic object on the Board, e.g. Characters, Items, PC, objects the player can use. Notably,
  * BoardObjects are Drawable, so they must define those abstract methods. */
trait BoardObject extends FrameByFrameAnimation {
  /** Returns the object's width. */
  override def getObjectWidth: Int = Board.TILE_SIZE

  /** Returns the object's height. */
  override def getObjectHeight: Int = Board.TILE_SIZE
}
