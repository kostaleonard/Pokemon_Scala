package model.board.cells

import java.awt.image.BufferedImage

import model.board.{Board, BoardObject}
import view.Drawable

abstract class Cell extends Drawable {
  protected var boardObject: Option[BoardObject] = None

  /** Returns the Cell's BoardObject. */
  def getBoardObject: Option[BoardObject] = boardObject

  /** Sets the stored BoardObject to the given value. */
  def setBoardObject(obj: Option[BoardObject]): Unit = boardObject = obj

  /** Returns the object's width. */
  def getObjectWidth: Int = Board.TILE_SIZE

  /** Returns the object's height. */
  def getObjectHeight: Int = Board.TILE_SIZE

  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  def getImage: BufferedImage
}
