package model.battle

import model.actor.{PlayerCharacter, Trainer}
import model.pokemon.Pokemon
import model.pokemon.move._

import scala.util.Random

class Battle(player: PlayerCharacter, opponent: Option[Trainer], wildPokemon: Option[Pokemon]) {
  if(opponent.isEmpty && wildPokemon.isEmpty) throw new UnsupportedOperationException("No foe pokemon defined; " +
    "both opponent and wildPokemon are None.")
  if(opponent.nonEmpty && wildPokemon.nonEmpty) throw new UnsupportedOperationException("Cannot have trainer and " +
    "wild pokemon set.")
  protected var playerPokemon: Pokemon = player.getParty.getNextPokemon.get
  protected var opponentPokemon: Pokemon =
    if(opponent.isEmpty) wildPokemon.get else opponent.get.getParty.getNextPokemon.get
  protected val seenOpponentPokemon: scala.collection.mutable.Set[Pokemon] = scala.collection.mutable.Set(playerPokemon)
  //TODO support for double battles.

  /** Returns the player's current pokemon. */
  def getPlayerPokemon: Pokemon = playerPokemon

  /** Returns the opponent pokemon. */
  def getOpponentPokemon: Pokemon = opponentPokemon

  /** Returns the set of all pokemon that the opponent pokemon has seen in battle. This is for experience
    * distribution. */
  def getSeenOpponentPokemon: scala.collection.mutable.Set[Pokemon] = seenOpponentPokemon

  /** Returns true if the opponent pokemon is wild. */
  def isOpponentWild: Boolean = wildPokemon.nonEmpty

  /** Returns true if the battle is over. */
  def isOver: Boolean = player.getParty.isWhiteout ||
    (opponent.nonEmpty && opponent.get.getParty.isWhiteout) ||
    (wildPokemon.nonEmpty && wildPokemon.get.isKO)

  /** Wraps up the battle; resets stats and removes non-persistent effects. */
  def endBattle(): Unit = {
    player.getParty.resetAfterBattle()
    if(opponent.nonEmpty) opponent.get.getParty.resetOnHeal()
  }

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
    //TODO I know the below is wrong because the after move effects get processed even if the move never happens.
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
