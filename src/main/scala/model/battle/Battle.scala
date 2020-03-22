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
  def makePlayerMove(move: Move): MoveSpecificationCollection = makePokemonMove(playerPokemon, opponentPokemon, move)

  /** Makes the opponent's move. */
  def makeOpponentMove(): MoveSpecificationCollection = {
    //TODO support for more opponent behaviors, not just random.
    val move = chooseRandomMove(opponentPokemon.getMoveList.getUsableMoves)
    makePokemonMove(opponentPokemon, playerPokemon, move)
  }

  /** Makes the move for the specified Pokemon. */
  protected def makePokemonMove(movingPokemon: Pokemon, otherPokemon: Pokemon, move: Move): MoveSpecificationCollection = {
    val beforeMoveEffectEvents = movingPokemon.getEffectTracker.getEventsFromBeforeMoveEffects(movingPokemon,
      otherPokemon)
    if(beforeMoveEffectEvents.contains(EndMove))
      return MoveSpecificationCollection(
        createMoveSpecifications(beforeMoveEffectEvents, movingPokemon, otherPokemon),
        Array.empty
      )
    var moveEvents = move.getEventsFromMove(movingPokemon, otherPokemon)
    if(moveEvents.contains(EndMove)) moveEvents = moveEvents.take(moveEvents.indexOf(EndMove))
    move.decrementPP()
    MoveSpecificationCollection(
      createMoveSpecifications(beforeMoveEffectEvents, movingPokemon, otherPokemon),
      createMoveSpecifications(moveEvents, movingPokemon, otherPokemon)
    )
  }

  /** Returns a List of MoveSpecifications that has HP bar animations added for display purposes. */
  def addHPBarAnimations(moveSpecifications: List[MoveSpecification]): List[MoveSpecification] =
    moveSpecifications.flatMap{
      case MoveSpecification(DealDamageToOpponent(amount), movingPokemon, otherPokemon) =>
        Array(MoveSpecification(PlayHPBarAnimation(otherPokemon, amount), movingPokemon, otherPokemon),
          MoveSpecification(DealDamageToOpponent(amount), movingPokemon, otherPokemon))
      case MoveSpecification(DealDamageToSelf(amount), movingPokemon, otherPokemon) =>
        Array(MoveSpecification(PlayHPBarAnimation(movingPokemon, amount), movingPokemon, otherPokemon),
          MoveSpecification(DealDamageToSelf(amount), movingPokemon, otherPokemon))
      case other => Array(other)
  }

  /** Returns a new list of move specifications, removing events as necessary if an EndMove or EndMoveOther signal was
    * sent. */
  def checkForEndMove(orderedMoveSpecifications: List[MoveSpecification]): List[MoveSpecification] = {
    var endMovePlayer = false
    var endMoveOpponent = false
    orderedMoveSpecifications.filter { specification =>
      if(specification.moveEvent == EndMove && specification.movingPokemon == playerPokemon)
        endMovePlayer = true
      else if(specification.moveEvent == EndMove && specification.movingPokemon == opponentPokemon)
        endMoveOpponent = true
      else if(specification.moveEvent == EndMoveOther && specification.otherPokemon == playerPokemon)
        endMovePlayer = true
      else if(specification.moveEvent == EndMoveOther && specification.otherPokemon == opponentPokemon)
        endMoveOpponent = true
      (specification.movingPokemon == playerPokemon && !endMovePlayer) ||
        (specification.movingPokemon == opponentPokemon && !endMoveOpponent)
    }
  }

  /** Returns a new list of move specifications, regenerating move events when specified by move events. */
  def checkForRegenerateMove(orderedMoveSpecifications: List[MoveSpecification]): List[MoveSpecification] =
    if(!orderedMoveSpecifications.map(_.moveEvent).contains(RegenerateMoveEventsOther)) orderedMoveSpecifications
    else {
      val eventsBeforeRegeneration = orderedMoveSpecifications.takeWhile(_.moveEvent != RegenerateMoveEventsOther)
      ???
  }

  /** Returns a new List of MoveSpecifications in the correct order for battle.  */
  def reorderMoveSpecifications(playerMoveSpecifications: MoveSpecificationCollection,
                                opponentMoveSpecifications: MoveSpecificationCollection): List[MoveSpecification] = {
    if(playerPokemon.getCurrentStats.getSpeed >= opponentPokemon.getCurrentStats.getSpeed)
      (playerMoveSpecifications.beforeMoveSpecs ++ playerMoveSpecifications.duringMoveSpecs ++
      opponentMoveSpecifications.beforeMoveSpecs ++ opponentMoveSpecifications.duringMoveSpecs).toList
    else
      (opponentMoveSpecifications.beforeMoveSpecs ++ opponentMoveSpecifications.duringMoveSpecs ++
      playerMoveSpecifications.beforeMoveSpecs ++ playerMoveSpecifications.duringMoveSpecs).toList
  }

  /** Returns the MoveSpecifications from the effects that happen after the moves. The opponent always receives the
    * effects first. */
  def getAfterMoveSpecifications: Array[MoveSpecification] = {
    val opponentEffectEvents = opponentPokemon.getEffectTracker.getEventsFromAfterMoveEffects(opponentPokemon,
      playerPokemon)
    val playerEffectEvents = playerPokemon.getEffectTracker.getEventsFromAfterMoveEffects(playerPokemon,
      opponentPokemon)
    createMoveSpecifications(opponentEffectEvents, opponentPokemon, playerPokemon) ++
    createMoveSpecifications(playerEffectEvents, playerPokemon, opponentPokemon)
  }

  //TODO this should be in an AI class or something.
  /** Returns a random move from the Array. */
  protected def chooseRandomMove(moves: Array[Move]): Move = moves(Random.nextInt(moves.length))

  /** Returns an Array of MoveSpecifications to match the events. */
  def createMoveSpecifications(events: Array[MoveEvent], movingPokemon: Pokemon, otherPokemon: Pokemon):
    Array[MoveSpecification] = events.map(MoveSpecification(_, movingPokemon, otherPokemon))
}
