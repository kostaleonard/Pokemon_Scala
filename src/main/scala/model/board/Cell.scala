package model.board

class Cell(protected val terrain: Terrain) {
  protected var boardObject: Option[BoardObject] = None

  /** Returns the Cell's Terrain. */
  def getTerrain: Terrain = terrain

  /** Returns the Cell's BoardObject. */
  def getBoardObject: Option[BoardObject] = boardObject

  /** Sets the stored BoardObject to the given value. */
  def setBoardObject(obj: Option[BoardObject]): Unit = boardObject = obj
}
