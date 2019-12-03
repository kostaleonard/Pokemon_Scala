package model.battle

import model.pokemon.Pokemon
import model.pokemon.move._

import scala.util.Random

class Battle(playerPokemon: Pokemon, opponentPokemon: Pokemon) {
  //TODO support for double battles.

  /** Makes the player's move. */
  def makePlayerMove(move: Move): Unit = makePokemonMove(playerPokemon, opponentPokemon, move)

  /** Makes the opponent's move. */
  def makeOpponentMove: Unit = {
    //TODO support for more opponent behaviors, not just random.
    val move = chooseRandomMove(opponentPokemon.getMoveList.getUsableMoves)
    makePokemonMove(opponentPokemon, playerPokemon, move)
  }

  /** Makes the move for the specified Pokemon. */
  protected def makePokemonMove(movingPokemon: Pokemon, otherPokemon: Pokemon, move: Move): Unit = {
    val beforeMoveEffectEvents = movingPokemon.getEffectTracker.getEventsFromBeforeMoveEffects(movingPokemon,
      otherPokemon)
    processEvents(beforeMoveEffectEvents, movingPokemon, otherPokemon)
    if(beforeMoveEffectEvents.contains(EndMove)) return
    val moveEvents = move.getEventsFromMove(movingPokemon, otherPokemon)
    processEvents(moveEvents, movingPokemon, otherPokemon)
    move.decrementPP
    val afterMoveEffectEvents = movingPokemon.getEffectTracker.getEventsFromAfterMoveEffects(movingPokemon,
      otherPokemon)
    processEvents(afterMoveEffectEvents, movingPokemon, otherPokemon)
  }

  /** Processes the events so that the move or effects are complete. thisPokemon is the Pokemon using the move,
    * otherPokemon is the other. */
  def processEvents(events: Array[MoveEvent], thisPokemon: Pokemon, otherPokemon: Pokemon): Unit = {
    //Handle special events.
    events.foreach { event => event match {
        case DisplayMessage(message) => System.out.println(message) //TODO will probably need to store in message buffer for view. Also need to wait for user to cycle through messages.
        case PlayAnimation(path) => System.out.println("Animation at %s".format(path)) //TODO play animation.
        case EndMove => return
        case _ => ;
      }
      event.doEvent(thisPokemon, otherPokemon)
    }
  }

  //TODO this should be in an AI class or something.
  /** Returns a random move from the Array. */
  protected def chooseRandomMove(moves: Array[Move]): Move = moves(Random.nextInt(moves.length))
}
