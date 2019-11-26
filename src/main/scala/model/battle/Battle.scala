package model.battle

import model.pokemon.Pokemon
import model.pokemon.move._

import scala.util.Random

class Battle(playerPokemon: Pokemon, opponentPokemon: Pokemon) {
  //TODO support for double battles.

  /** Makes the opponent's move. */
  def makeOpponentMove: Unit = {
    //TODO support for more opponent behaviors, not just random.
    val move = chooseRandomMove(opponentPokemon.getMoveList.getUsableMoves)
    val beforeMoveEffectEvents = opponentPokemon.getEffectTracker.getEventsFromBeforeMoveEffects(opponentPokemon,
      playerPokemon)
    processEvents(beforeMoveEffectEvents, opponentPokemon, playerPokemon)
    if(beforeMoveEffectEvents.contains(EndMove)) return
    val moveEvents = move.getEventsFromMove(opponentPokemon, playerPokemon)
    processEvents(moveEvents, opponentPokemon, playerPokemon)
    move.decrementPP
    val afterMoveEffectEvents = opponentPokemon.getEffectTracker.getEventsFromAfterMoveEffects(opponentPokemon,
      playerPokemon)
    processEvents(afterMoveEffectEvents, opponentPokemon, playerPokemon)
  }

  /** Processes the events so that the move or effects are complete. thisPokemon is the Pokemon using the move,
    * otherPokemon is the other. */
  def processEvents(events: Array[MoveEvent], thisPokemon: Pokemon, otherPokemon: Pokemon): Unit = {
    events.foreach { event => event match {
        case DisplayMessage(message) => System.out.println(message) //TODO will probably need to store in message buffer for view. Also need to wait for user to cycle through messages.
        case PlayAnimation(path) => System.out.println("Animation at %s".format(path)) //TODO play animation.
        case EndMove => return
      }
      event.doEvent(thisPokemon, otherPokemon)
    }
  }

  //TODO this should be in an AI class or something.
  /** Returns a random move from the Array. */
  protected def chooseRandomMove(moves: Array[Move]): Move = moves(Random.nextInt(moves.length))
}
