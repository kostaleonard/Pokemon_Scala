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

  /** Returns a random move from the opponent pokemon. */
  def getRandomOpponentMove: Move = chooseRandomMove(opponentPokemon.getMoveList.getUsableMoves)

  /** Makes the player's move. */
  def getPlayerMoveSpecifications(move: Move): List[MoveSpecification] =
    getMoveSpecifications(playerPokemon, opponentPokemon, move)

  /** Makes the opponent's move. */
  def getOpponentMoveSpecifications(move: Move): List[MoveSpecification] =
    getMoveSpecifications(opponentPokemon, playerPokemon, move)

  /** Returns the finalized list of MoveSpecifications to be used in battle. */
  protected def cleanMoveSpecifications(moveSpecifications: List[MoveSpecification]): List[MoveSpecification] =
    addHPBarAnimations(moveSpecifications)

  /** Makes the move for the specified Pokemon. */
  protected def getMoveSpecifications(movingPokemon: Pokemon, otherPokemon: Pokemon, move: Move):
  List[MoveSpecification] = {
    val beforeMoveEffectEvents = movingPokemon.getEffectTracker.getEventsFromBeforeMoveEffects(movingPokemon,
      otherPokemon)
    if(beforeMoveEffectEvents.contains(EndMove))
      return cleanMoveSpecifications(createMoveSpecifications(
        beforeMoveEffectEvents, movingPokemon, otherPokemon).toList)
    var moveEvents = move.getEventsFromMove(movingPokemon, otherPokemon)
    if(moveEvents.contains(EndMove)) moveEvents = moveEvents.take(moveEvents.indexOf(EndMove))
    cleanMoveSpecifications(
      (createMoveSpecifications(beforeMoveEffectEvents, movingPokemon, otherPokemon) ++
      createMoveSpecifications(DecrementPP(move) +: moveEvents, movingPokemon, otherPokemon)).toList)
  }

  //TODO probably make a MoveChoice trait for this so that you can handle it right. Moves are MoveChoices, but so are using Items and running away.
  /** Returns the first mover (the player or opponent pokemon) based on current speed and any special moves chosen. */
  def getBattleOrder(playerMove: Move, opponentMove: Move): (Pokemon, Pokemon) = (playerMove, opponentMove) match {
    case (_, _) =>
      if(playerPokemon.getCurrentStats.getSpeed >= opponentPokemon.getCurrentStats.getSpeed)
        (playerPokemon, opponentPokemon)
      else
        (opponentPokemon, playerPokemon)
  }

  /** Returns a List of MoveSpecifications that has HP bar animations added for display purposes. */
  protected def addHPBarAnimations(moveSpecifications: List[MoveSpecification]): List[MoveSpecification] =
    moveSpecifications.flatMap{
      case MoveSpecification(DealDamageToOpponent(amount), movingPokemon, otherPokemon) =>
        Array(MoveSpecification(PlayHPBarAnimation(otherPokemon, amount), movingPokemon, otherPokemon),
          MoveSpecification(DealDamageToOpponent(amount), movingPokemon, otherPokemon))
      case MoveSpecification(DealDamageToSelf(amount), movingPokemon, otherPokemon) =>
        Array(MoveSpecification(PlayHPBarAnimation(movingPokemon, amount), movingPokemon, otherPokemon),
          MoveSpecification(DealDamageToSelf(amount), movingPokemon, otherPokemon))
      case MoveSpecification(HealOther(amount), movingPokemon, otherPokemon) =>
        Array(MoveSpecification(PlayHPBarAnimation(otherPokemon, -amount), movingPokemon, otherPokemon),
          MoveSpecification(HealOther(amount), movingPokemon, otherPokemon))
      case MoveSpecification(HealSelf(amount), movingPokemon, otherPokemon) =>
        Array(MoveSpecification(PlayHPBarAnimation(movingPokemon, -amount), movingPokemon, otherPokemon),
          MoveSpecification(HealSelf(amount), movingPokemon, otherPokemon))
      case other => Array(other)
  }

  /** Returns the MoveSpecifications from the effects that happen after the moves. The opponent always receives the
    * effects first. */
  def getAfterMoveSpecifications: List[MoveSpecification] = {
    val opponentEffectEvents = opponentPokemon.getEffectTracker.getEventsFromAfterMoveEffects(opponentPokemon,
      playerPokemon)
    val playerEffectEvents = playerPokemon.getEffectTracker.getEventsFromAfterMoveEffects(playerPokemon,
      opponentPokemon)
    cleanMoveSpecifications((createMoveSpecifications(opponentEffectEvents, opponentPokemon, playerPokemon) ++
      createMoveSpecifications(playerEffectEvents, playerPokemon, opponentPokemon)).toList)
  }

  //TODO this should be in an AI class or something.
  /** Returns a random move from the Array. */
  protected def chooseRandomMove(moves: Array[Move]): Move = moves(Random.nextInt(moves.length))

  /** Returns an Array of MoveSpecifications to match the events. */
  protected def createMoveSpecifications(events: Array[MoveEvent], movingPokemon: Pokemon, otherPokemon: Pokemon):
    Array[MoveSpecification] = events.map(MoveSpecification(_, movingPokemon, otherPokemon))
}
