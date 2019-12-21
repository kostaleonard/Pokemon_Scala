package model.board

class Cell(protected val terrain: Terrain) {
  protected var boardObject: Option[BoardObject] = None
}
