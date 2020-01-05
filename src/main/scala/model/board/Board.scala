package model.board

import java.awt.{Color, Graphics2D}
import java.awt.image.BufferedImage

import model.board.cells._
import view.Drawable

object Board {
  val TILE_SIZE = 50

  /** Returns a test board. */
  def getTestBoard1: Board = {
    val cells: Array[Array[Cell]] = Array.fill(10)(Array.fill(10)(new LowGrass))
    val board = new Board(cells)
    board
  }

  /** Returns a test board. */
  def getTestBoard2: Board = {
    val cells: Array[Array[Cell]] =
      (0 until 50).map { r =>
        (0 until 75).map { c =>
          if(r > 10 && r < 13) new ConcretePavement
          else if(r < 8 && c < 10) new TallGrass
          else if(c < 30 && c % 4 == 0 && r % 2 == 0) new Dirt
          else new LowGrass
      }.toArray
    }.toArray
    val board = new Board(cells)
    board
  }
}

class Board(protected val cells: Array[Array[Cell]], protected val spawnLocation: Option[Location] = None)
  extends Drawable{
  /** Returns the cells on the Board. */
  def getCells: Array[Array[Cell]] = cells

  /** Returns the Location at which the player will spawn if they warp to this Board, or if they start the game on
    * it. If None and the player tries to spawn on this Board, behavior is undefined. */
  def getSpawnLocation: Option[Location] = spawnLocation

  /** Sets the BoardObject at the given Location to obj, which is optional. */
  def placeObjectAt(obj: Option[BoardObject], location: Location): Unit =
    cells(location.row)(location.col).setBoardObject(obj)

  /** Returns the object's width. */
  override def getObjectWidth: Int = Board.TILE_SIZE * cells.head.length

  /** Returns the object's height. */
  def getObjectHeight: Int = Board.TILE_SIZE * cells.length

  //TODO you'll have to do a bit of gymnastics to get animations to work right with board objects.
  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  def getImage: BufferedImage = {
    val g2d = canvasImage.getGraphics.asInstanceOf[Graphics2D]
    /** Draw cells. */
    cells.indices.foreach { r =>
      cells(r).indices.foreach { c =>
        g2d.drawImage(cells(r)(c).getImage,
          c * Board.TILE_SIZE, r * Board.TILE_SIZE,
          cells(r)(c).getObjectWidth, cells(r)(c).getObjectHeight,
          null)
      }
    }
    /** Draw board objects. */
    cells.indices.foreach { r =>
      cells(r).indices.foreach { c =>
        cells(r)(c).getBoardObject.map(obj =>
          g2d.drawImage(obj.getImage,
            c * Board.TILE_SIZE, r * Board.TILE_SIZE,
            obj.getObjectWidth, obj.getObjectHeight,
            null)
        )
      }
    }
    g2d.dispose()
    canvasImage
  }
}
