package model.board

import java.awt.{Color, Graphics2D}
import java.awt.image.BufferedImage

import model.board.cells._
import model.actor.{Actor, Trainer}
import model.battle.Battle
import model.item.Item
import view.View
import view.views.drawing.{Animation, Drawable}

object Board {
  val TILE_SIZE = 64
  val NUM_ROWS_RENDERED: Int = View.FRAME_DESIGN_HEIGHT / TILE_SIZE
  val NUM_COLS_RENDERED: Int = View.FRAME_DESIGN_WIDTH / TILE_SIZE

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
          else if(r < 10 && c > 0 && c < 14) new TallGrass
          else if(c < 30 && c % 4 == 0 && r % 2 == 0) new Dirt
          else new LowGrass
      }.toArray
    }.toArray
    val board = new Board(cells)
    board
  }
}

class Board(protected val cells: Array[Array[Cell]], protected val spawnLocation: Option[Location] = None)
  extends Animation {
  //TODO this would be a cool use case for a new data structure.
  //TODO maybe this is petty, but I really want to write a combinator for this so I don't have to call buildBoardObjectMap().
  /** The boardObjectMap provides an O(1) mapping to get the Location of any given BoardObject. It is the inverse
    * operation of retrieving the BoardObject at any given Location, which can be done by accessing cells. This is
    * redundant information, but is necessary to ensure fast access. */
  protected val boardObjectMap: scala.collection.mutable.Map[BoardObject, Location] =
    scala.collection.mutable.Map.empty[BoardObject, Location]
  buildBoardObjectMap()
  protected var centeredLocation: Location = spawnLocation.getOrElse(Location(0, 0))
  protected var canvasImage: BufferedImage = new BufferedImage(getObjectWidth, getObjectHeight,
    BufferedImage.TYPE_INT_ARGB)

  /** Builds the boardObjectMap. */
  protected def buildBoardObjectMap(): Unit = {
    cells.indices.foreach(r =>
      cells(r).indices.foreach(c =>
        cells(r)(c).getBoardObject.map(boardObjectMap(_) = Location(r, c))
      )
    )
  }

  /** Returns the location on which the board is centered. */
  def getCenteredLocation: Location = centeredLocation

  /** Sets the location on which the board is centered. */
  def setCenteredLocation(loc: Location): Unit = centeredLocation = loc

  /** Returns the cells on the Board. */
  def getCells: Array[Array[Cell]] = cells

  /** Returns the Location at which the player will spawn if they warp to this Board, or if they start the game on
    * it. If None and the player tries to spawn on this Board, behavior is undefined. */
  def getSpawnLocation: Option[Location] = spawnLocation

  /** Sets the BoardObject at the given Location to obj, which is optional. Also updates the boardObjectMap. */
  def setBoardObjectAt(location: Location, obj: Option[BoardObject]): Unit = {
    val oldObject = cells(location.row)(location.col).getBoardObject
    cells(location.row)(location.col).setBoardObject(obj)
    if(obj.nonEmpty) boardObjectMap(obj.get) = location
    if(oldObject.nonEmpty) boardObjectMap.remove(oldObject.get)
  }

  /** Returns an Array of all objects on the board. */
  def getBoardObjects: Array[BoardObject] = boardObjectMap.keys.toArray

  /** Returns the location of obj on the board, or None if not present. */
  def getBoardObjectLocation(obj: BoardObject): Option[Location] = boardObjectMap.get(obj)

  /** Returns true if the location is a valid spot on the board. */
  def isValidLocation(loc: Location): Boolean = loc.row >= 0 && loc.col >= 0 &&
    loc.row < cells.length && loc.col < cells.head.length

  /** Returns the destination of the actor's movement in a given direction. */
  def getActorDestination(actor: Actor, direction: Direction): Location = {
    val actorLoc = getBoardObjectLocation(actor)
    if(actorLoc.isEmpty) throw new UnsupportedOperationException("Actor not found on board.")
    if(actor.getFacingDirection != direction) actorLoc.get
    else {
      val destination = direction match {
        case North => Location(actorLoc.get.row - 1, actorLoc.get.col)
        case East => Location(actorLoc.get.row, actorLoc.get.col + 1)
        case South => Location(actorLoc.get.row + 1, actorLoc.get.col)
        case West => Location(actorLoc.get.row, actorLoc.get.col - 1)
      }
      if (isValidLocation(destination)) destination else actorLoc.get
    }
  }

  /** Moves the actor in the given direction. */
  protected def moveActor(actor: Actor, direction: Direction, encounter: Option[Battle]): Unit = {
    val actorLoc = getBoardObjectLocation(actor)
    if(actorLoc.isEmpty) throw new UnsupportedOperationException("Actor not found on board.")
    val destination = direction match {
      case North => Location(actorLoc.get.row - 1, actorLoc.get.col)
      case East => Location(actorLoc.get.row, actorLoc.get.col + 1)
      case South => Location(actorLoc.get.row + 1, actorLoc.get.col)
      case West => Location(actorLoc.get.row, actorLoc.get.col - 1)
    }
    if(isValidLocation(destination)) {
      setBoardObjectAt(actorLoc.get, None)
      setBoardObjectAt(destination, Some(actor))
      val xOffset = destination.col * Board.TILE_SIZE - actorLoc.get.col * Board.TILE_SIZE
      val yOffset = destination.row * Board.TILE_SIZE - actorLoc.get.row * Board.TILE_SIZE
      actor.setDrawOffsetX(xOffset)
      actor.setDrawOffsetY(yOffset)
      actor.alternateStep()
    }
  }

  /** Turns the actor in the given direction. */
  protected def turnActor(actor: Actor, direction: Direction): Unit = actor.setFacingDirection(direction)

  /** Sends the actor in the given direction. If they are not facing this direction, performs a turn; if they are,
    * performs a move. */
  def sendActorInDirection(actor: Actor, direction: Direction, encounter: Option[Battle]): Unit = {
    if(actor.isMoving) throw new UnsupportedOperationException("Cannot move actor when they are already moving.")
    if(actor.getFacingDirection == direction) moveActor(actor, direction, encounter)
    else turnActor(actor, direction)
  }

  /** Returns the object's width. */
  override def getObjectWidth: Int = Board.TILE_SIZE * cells.head.length

  /** Returns the object's height. */
  override def getObjectHeight: Int = Board.TILE_SIZE * cells.length

  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  def getImage: BufferedImage = {
    val g2d = canvasImage.getGraphics.asInstanceOf[Graphics2D]
    val startRow = 0 max (centeredLocation.row - Board.NUM_ROWS_RENDERED / 2)
    val endRow = (cells.length - 1) min (startRow + Board.NUM_ROWS_RENDERED)
    val startCol = 0 max (centeredLocation.col - Board.NUM_COLS_RENDERED / 2)
    val endCol = (cells.head.length - 1) min (startCol + Board.NUM_COLS_RENDERED)
    /** Draw cells. */
    cells.indices.slice(startRow, endRow + 1).foreach { r =>
      cells(r).indices.slice(startCol, endCol + 1).foreach { c =>
        g2d.drawImage(cells(r)(c).getImage, c * Board.TILE_SIZE, r * Board.TILE_SIZE, null)
      }
    }
    /** Draw board objects. */
    cells.indices.slice(startRow, endRow + 1).foreach { r =>
      cells(r).indices.slice(startCol, endCol + 1).foreach { c =>
        cells(r)(c).getBoardObject.map(obj =>
          g2d.drawImage(obj.getImage,
            c * Board.TILE_SIZE - obj.getDrawOffsetX, r * Board.TILE_SIZE - obj.getDrawOffsetY, null)
        )
      }
    }
    g2d.dispose()
    canvasImage
  }

  /** Returns the object's image, already scaled. This is to speed up rendering. */
  def getPrescaledImage: Option[BufferedImage] = None

  /** Returns true if the animation is complete. */
  def isAnimationComplete: Boolean = false

  /** Progresses animations by one frame. Parent objects should call on all child objects they render. */
  override def advanceFrame(): Unit = {
    getBoardObjects.foreach(_.advanceFrame())
  }
}
