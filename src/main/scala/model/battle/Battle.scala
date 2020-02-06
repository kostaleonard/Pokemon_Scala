package model.battle

import model.actor.{PlayerCharacter, Trainer}
import model.pokemon.Pokemon
import model.pokemon.move._

import scala.util.Random

class Battle(player: PlayerCharacter, opponent: Option[Trainer], wildPokemon: Option[Pokemon]) {
  if(opponent.isEmpty && wildPokemon.isEmpty) throw new UnsupportedOperationException("No foe pokemon defined; " +
    "both opponent and wildPokemon are None.")
  protected var playerPokemon: Pokemon = player.getParty.getNextPokemon.get
  protected var opponentPokemon: Pokemon =
    if(opponent.isEmpty) wildPokemon.get else opponent.get.getParty.getNextPokemon.get
  //TODO support for double battles.

  /** Returns the player's current pokemon. */
  def getPlayerPokemon: Pokemon = playerPokemon

  /** Returns the opponent pokemon. */
  def getOpponentPokemon: Pokemon = opponentPokemon

  /** Returns true if the battle is over. */
  def isOver: Boolean = player.getParty.isWhiteout ||
    (opponent.nonEmpty && opponent.get.getParty.isWhiteout) ||
    (wildPokemon.nonEmpty && wildPokemon.get.isKO)

  /** Makes the player's move. */
  def makePlayerMove(move: Move): Array[MoveSpecification] = makePokemonMove(playerPokemon, opponentPokemon, move)

  /** Makes the opponent's move. */
  def makeOpponentMove(): Array[MoveSpecification] = {
    //TODO support for more opponent behaviors, not just random.
    val move = chooseRandomMove(opponentPokemon.getMoveList.getUsableMoves)
    makePokemonMove(opponentPokemon, playerPokemon, move)
  }

  /** Makes the move for the specified Pokemon. */
  protected def makePokemonMove(movingPokemon: Pokemon, otherPokemon: Pokemon, move: Move): Array[MoveSpecification] = {
    val beforeMoveEffectEvents = movingPokemon.getEffectTracker.getEventsFromBeforeMoveEffects(movingPokemon,
      otherPokemon)
    //processEvents(beforeMoveEffectEvents, movingPokemon, otherPokemon)
    if(beforeMoveEffectEvents.contains(EndMove))
      return createMoveSpecifications(beforeMoveEffectEvents, movingPokemon, otherPokemon)
    val moveEvents = move.getEventsFromMove(movingPokemon, otherPokemon)
    //processEvents(moveEvents, movingPokemon, otherPokemon)
    move.decrementPP
    val afterMoveEffectEvents = movingPokemon.getEffectTracker.getEventsFromAfterMoveEffects(movingPokemon,
      otherPokemon)
    //processEvents(afterMoveEffectEvents, movingPokemon, otherPokemon)
    createMoveSpecifications(beforeMoveEffectEvents ++ moveEvents ++ afterMoveEffectEvents, movingPokemon, otherPokemon)
  }

  //TODO this should be in an AI class or something.
  /** Returns a random move from the Array. */
  protected def chooseRandomMove(moves: Array[Move]): Move = moves(Random.nextInt(moves.length))

  /** Returns an Array of MoveSpecifications to match the events. */
  protected def createMoveSpecifications(events: Array[MoveEvent], movingPokemon: Pokemon, otherPokemon: Pokemon):
    Array[MoveSpecification] = events.map(MoveSpecification(_, movingPokemon, otherPokemon))
}
