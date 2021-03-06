package model.pokemon.move


object MoveList {
  val MAX_SIZE = 4
}

//TODO consider refactor: rename MoveList to MoveArray (since under the hood backed by Array, not List).
class MoveList(protected val initialMoves: Array[Move]) {
  if(initialMoves.isEmpty) throw new UnsupportedOperationException("Cannot instantiate MoveList with empty moves.")
  protected val moves: Array[Option[Move]] = (0 until MoveList.MAX_SIZE).map(i =>
   if(i < initialMoves.length) Some(initialMoves(i))
   else None
  ).toArray

  /** Sets the move at index to the given move. */
  def setMove(index: Int, move: Move): Unit =
    if(index < 0 && index >= moves.length)
      throw new UnsupportedOperationException("Attempting to access outside of Array bounds.")
    else moves(index) = Some(move)

  /** Returns the Array of optional moves. This is used more for display purposes, because players need to know the
    * indices of Nones. */
  def getMovesOption: Array[Option[Move]] = moves

  /** Returns the Array of moves. */
  def getMoves: Array[Move] = moves.flatten

  /** Returns only the usable moves (the ones with PP). */
  def getUsableMoves: Array[Move] = {
    val usableMoves = moves.flatten.filter(_.canUse)
    if(usableMoves.isEmpty) ??? //TODO struggle.
    else usableMoves
  }
}
