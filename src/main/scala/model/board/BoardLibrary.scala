package model.board

object BoardLibrary {
  //TODO define starting boards.
  /** Returns the Map of Board names to Boards with which every new game begins. */
  def getStartingBoardsByName: Map[String, Board] = Map(
    //"ANTIETAM" -> ???,
    //"SNOWBLIND" -> ???
  )
}

/** Stores all of the Boards for a single game. */
class BoardLibrary {
  protected val boardsByName: Map[String, Board] = BoardLibrary.getStartingBoardsByName

  /** Returns the Board associated with a given name. */
  def getBoard(name: String): Board = boardsByName.getOrElse(name,
    throw new UnsupportedOperationException("No Board with name %s".format(name)))
}
