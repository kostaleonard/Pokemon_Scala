package model.board

object Board {
  /** Returns a test board. */
  def getTestBoard1: Board = {
    val cells = Array.fill(10)(Array.fill(10)(new Cell(TallGrass)))
    val board = new Board(cells)
    board
  }
}

class Board(protected val cells: Array[Array[Cell]], protected val spawnLocation: Option[Location] = None) {
  /** Returns the cells on the Board. */
  def getCells: Array[Array[Cell]] = cells

  /** Returns the Location at which the player will spawn if they warp to this Board, or if they start the game on
    * it. If None and the player tries to spawn on this Board, behavior is undefined. */
  def getSpawnLocation: Option[Location] = spawnLocation

  /** Sets the BoardObject at the given Location to obj, which is optional. */
  def placeObjectAt(obj: Option[BoardObject], location: Location): Unit =
    cells(location.row)(location.col).setBoardObject(obj)
}
