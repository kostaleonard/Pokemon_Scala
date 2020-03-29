package model.pokemon.move

import model.pokemon.Pokemon
import model.pokemon.stat.BattleStats
import model.statuseffect._
import view.views.drawing.Animation

import scala.collection.mutable.ListBuffer
import scala.util.Random

object MoveEventGenerator {
  val FAINT_ANIMATION_PATH = "TODO Fainted"
  /** Returns true if the damage dealt will cause the pokemon to KO. */
  def willDamageKO(pokemon: Pokemon, damage: Int): Boolean = pokemon.getCurrentStats.getHP <= damage

  /** Returns the list of MoveEvents used when a Pokemon KOs. */
  def getKOEvents(pokemon: Pokemon, isOther: Boolean): List[MoveEvent] = List(PlayAnimationFromSource(FAINT_ANIMATION_PATH),
    MoveEvent.getDisplayMessageFainted(pokemon.getName), if(isOther) FaintOther else FaintSelf)
}

sealed trait MoveEventGenerator {
  /** Does some action that occurs during a move (e.g. damage, effects, KO). Returns the List of MoveEvents that result
    * from this action. */
  def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent]
}

case class AccuracyCheck(accuracy: Double) extends MoveEventGenerator {
  //TODO the move may miss or hit based on effects on either of the pokemon.
  //TODO need to check accuracy stages.
  /** Checks to see if the move hits. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] =
    if(Random.nextDouble() > accuracy) List(MoveEvent.getDisplayMessageMoveMissed(thisPokemon.getName), EndMove)
    else List.empty
}

case class UseMultiMove(moves: List[Move]) extends MoveEventGenerator {
  /** Use moves in order over several turns. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] = {
    val multiMoveEffect = thisPokemon.getEffectTracker.getNonPersistentEffects.find(_.isInstanceOf[UsingMultiMove])
    if(multiMoveEffect.nonEmpty) thisPokemon.getEffectTracker.removeEffect(multiMoveEffect.get)
    if(moves.tail.nonEmpty) thisPokemon.getEffectTracker.addEffect(UsingMultiMove(moves.tail))
    moves.head.getEventsFromMove(thisPokemon, otherPokemon).toList
  }
}

case class AnimationGenerator(move: Move) extends MoveEventGenerator {
  /** Returns the animation to be played. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] = List(PlayMoveAnimation(move))
}

case class MoveDamage(move: Move) extends MoveEventGenerator {
  /** Deals damage to the other pokemon. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] = {
    val result = ListBuffer.empty[MoveEvent]

    val targets = 1 //TODO if multiple targets, this is 0.75.
    val weather = 1 //TODO 1.5 if water move in rain or fire move in harsh sunlight; 0.5 if water move in harsh sunlight or fire move in rain.
    val randomFactor = Random.nextDouble() * 0.15 + 0.85
    val stab = if(thisPokemon.getTypeArray.contains(move.getType)) 1.5 else 1
    val typeEffectiveness = otherPokemon.getTypeArray.foldRight(1.0)((otherType, accum) =>
      accum * move.getType.getTypeEffectiveness(otherType))
    val isCriticalHit = (Random.nextDouble() < move.getCriticalHitChance) && typeEffectiveness > 0
    var critical = if(isCriticalHit) 2 else 1
    val burned = if(thisPokemon.getEffectTracker.contains(Burn) && move.isPhysical) 0.5 else 1
    val other = 1 //TODO used in some moves.

    val modifier = targets * weather * critical * randomFactor * stab * typeEffectiveness * burned

    val A = if(move.isPhysical) thisPokemon.getCurrentStats.getAttack
      else thisPokemon.getCurrentStats.getSpecialAttack
    val D = if(move.isPhysical) otherPokemon.getCurrentStats.getDefense
      else otherPokemon.getCurrentStats.getSpecialDefense

    val damage = ((
      ((2.0 * thisPokemon.getLevel) / 5.0 * move.getPower.get * (A.toDouble / D.toDouble)) / 50.0 + 2.0
      ) * modifier).toInt max 1

    result.append(PlayMoveAnimation(move))
    result.append(DealDamageToOpponent(damage))
    if(isCriticalHit) result.append(MoveEvent.DISPLAY_CRITICAL_HIT)
    if(typeEffectiveness == 0) result.append(
      MoveEvent.getDisplayMessageMoveNoEffect(move.getName, otherPokemon.getName))
    else if(typeEffectiveness < 1) result.append(MoveEvent.DISPLAY_NOT_VERY_EFFECTIVE)
    else if(typeEffectiveness > 1) result.append(MoveEvent.DISPLAY_SUPER_EFFECTIVE)
    if(MoveEventGenerator.willDamageKO(otherPokemon, damage)) result ++=
      MoveEventGenerator.getKOEvents(otherPokemon, true)
    result.toList
  }
}

case object TakeInSunlight extends MoveEventGenerator {
  /** Takes in sunlight. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] =
    List(MoveEvent.getDisplayMessageTookInSunlight(thisPokemon.getName))
}

case class TryLowerStatOther(statKey: String, stages: Int, moveToAnimate: Option[Move]) extends MoveEventGenerator {
  /** Lowers the opponent's given stat by the given number of stages. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] = {
    val successCheck = () => otherPokemon.getCurrentStats.getStage(statKey) > BattleStats.MIN_STAGE
    val result = ListBuffer.empty[MoveEvent]
    if (moveToAnimate.nonEmpty) result.append(PlayMoveAnimation(moveToAnimate.get))
    result.append(LowerStatOther(statKey, stages))
    result.append(MoveEvent.getDisplayMessageStatFell(otherPokemon.getName, statKey))
    val eventsIfTrue = result.toList
    val eventsIfFalse = List(MoveEvent.getDisplayMessageStatWillNotGoLower(otherPokemon.getName, statKey))
    //List(SucceedOrFailEvent(successCheck, eventsIfTrue, eventsIfFalse, thisPokemon, otherPokemon))
    if(successCheck.apply()) eventsIfTrue else eventsIfFalse
  }
}

case class TryRaiseStatSelf(statKey: String, stages: Int, moveToAnimate: Option[Move]) extends MoveEventGenerator {
  /** Raises this Pokemon's given stat by the given number of stages. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] = {
    val successCheck = () => thisPokemon.getCurrentStats.getStage(statKey) < BattleStats.MAX_STAGE
    val result = ListBuffer.empty[MoveEvent]
    if (moveToAnimate.nonEmpty) result.append(PlayMoveAnimation(moveToAnimate.get))
    result.append(RaiseStatSelf(statKey, stages))
    result.append(MoveEvent.getDisplayMessageStatRose(thisPokemon.getName, statKey))
    val eventsIfTrue = result.toList
    val eventsIfFalse = List(MoveEvent.getDisplayMessageStatWillNotGoHigher(thisPokemon.getName, statKey))
    //List(SucceedOrFailEvent(successCheck, eventsIfTrue, eventsIfFalse, thisPokemon, otherPokemon))
    if(successCheck.apply()) eventsIfTrue else eventsIfFalse
  }
}

case class TryHealSelf(percentMaxHP: Double, moveToAnimate: Option[Move]) extends MoveEventGenerator {
  /** Raises this Pokemon's given stat by the given number of stages. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] = {
    val successCheck = () => thisPokemon.getCurrentStats.getHP < thisPokemon.getStandardStats.getHP
    val result = ListBuffer.empty[MoveEvent]
    if (moveToAnimate.nonEmpty) result.append(PlayMoveAnimation(moveToAnimate.get))
    val healAmount = (thisPokemon.getStandardStats.getHP * percentMaxHP).toInt
    result.append(HealSelf(healAmount))
    result.append(MoveEvent.getDisplayMessageHPRestored(thisPokemon.getName))
    val eventsIfTrue = result.toList
    val eventsIfFalse = List(MoveEvent.DISPLAY_BUT_IT_FAILED)
    //List(SucceedOrFailEvent(successCheck, eventsIfTrue, eventsIfFalse, thisPokemon, otherPokemon))
    if(successCheck.apply()) eventsIfTrue else eventsIfFalse
  }
}

case class TryBurn(probability: Double, displayFailure: Boolean, moveToAnimate: Option[Move])
  extends MoveEventGenerator {
  /** Returns a List containing an effect infliction event if successful; an empty list otherwise. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] = {
    val successCheck = () => otherPokemon.getEffectTracker.getPersistentEffect.isEmpty &&
      Random.nextDouble() < probability
    val animationEvents = if (moveToAnimate.isEmpty) List.empty else List(PlayMoveAnimation(moveToAnimate.get))
    val eventsIfTrue = animationEvents ++ List(MoveEvent.getDisplayMessageBurned(otherPokemon.getName),
      InflictEffectOnOpponent(Burn))
    val eventsIfFalse = if (displayFailure) List(MoveEvent.DISPLAY_BUT_IT_FAILED) else List.empty
    //List(SucceedOrFailEvent(successCheck, eventsIfTrue, eventsIfFalse, thisPokemon, otherPokemon))
    if(successCheck.apply()) eventsIfTrue else eventsIfFalse
  }
}

case class TryPoison(probability: Double, displayFailure: Boolean, badly: Boolean, moveToAnimate: Option[Move])
  extends MoveEventGenerator {
  /** Returns a List containing an effect infliction event if successful; an empty list otherwise. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] = {
    val successCheck = () => otherPokemon.getEffectTracker.getPersistentEffect.isEmpty &&
      Random.nextDouble() < probability
    val animationEvents = if (moveToAnimate.isEmpty) List.empty else List(PlayMoveAnimation(moveToAnimate.get))
    val eventsIfTrue = animationEvents ++ List(MoveEvent.getDisplayMessagePoisoned(otherPokemon.getName, badly),
      InflictEffectOnOpponent(Poison(badly, 1)))
    val eventsIfFalse = if (displayFailure) List(MoveEvent.DISPLAY_BUT_IT_FAILED) else List.empty
    //List(SucceedOrFailEvent(successCheck, eventsIfTrue, eventsIfFalse, thisPokemon, otherPokemon))
    if(successCheck.apply()) eventsIfTrue else eventsIfFalse
  }
}

case class TrySleep(probability: Double, displayFailure: Boolean, turns: Int, moveToAnimate: Option[Move])
  extends MoveEventGenerator {
  /** Returns a List containing an effect infliction event if successful; an empty list otherwise. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] = {
    val successCheck = () => otherPokemon.getEffectTracker.getPersistentEffect.isEmpty &&
      Random.nextDouble() < probability
    val animationEvents = if (moveToAnimate.isEmpty) List.empty else List(PlayMoveAnimation(moveToAnimate.get))
    val eventsIfTrue = animationEvents ++ List(MoveEvent.getDisplayMessageFellAsleep(otherPokemon.getName),
      InflictEffectOnOpponent(Sleep(turns)))
    val eventsIfFalse = if(displayFailure) List(MoveEvent.DISPLAY_BUT_IT_FAILED) else List.empty
    //List(SucceedOrFailEvent(successCheck, eventsIfTrue, eventsIfFalse, thisPokemon, otherPokemon))
    if(successCheck.apply()) eventsIfTrue else eventsIfFalse
  }
}

case class TryParalyze(probability: Double, displayFailure: Boolean, moveToAnimate: Option[Move])
  extends MoveEventGenerator {
  /** Returns a List containing an effect infliction event if successful; an empty list otherwise. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] = {
    val successCheck = () => otherPokemon.getEffectTracker.getPersistentEffect.isEmpty &&
      Random.nextDouble() < probability
    val animationEvents = if (moveToAnimate.isEmpty) List.empty else List(PlayMoveAnimation(moveToAnimate.get))
    val eventsIfTrue = animationEvents ++ List(MoveEvent.getDisplayMessageParalyzed(otherPokemon.getName),
      InflictEffectOnOpponent(Paralyze))
    val eventsIfFalse = if(displayFailure) List(MoveEvent.DISPLAY_BUT_IT_FAILED) else List.empty
    //List(SucceedOrFailEvent(successCheck, eventsIfTrue, eventsIfFalse, thisPokemon, otherPokemon))
    if(successCheck.apply()) eventsIfTrue else eventsIfFalse
  }
}

case class TryFreeze(probability: Double, displayFailure: Boolean, moveToAnimate: Option[Move])
  extends MoveEventGenerator {
  /** Returns a List containing an effect infliction event if successful; an empty list otherwise. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] = {
    val successCheck = () => otherPokemon.getEffectTracker.getPersistentEffect.isEmpty &&
      Random.nextDouble() < probability
    val animationEvents = if (moveToAnimate.isEmpty) List.empty else List(PlayMoveAnimation(moveToAnimate.get))
    val eventsIfTrue = animationEvents ++ List(MoveEvent.getDisplayMessageFrozen(otherPokemon.getName),
      InflictEffectOnOpponent(Frozen))
    val eventsIfFalse = if(displayFailure) List(MoveEvent.DISPLAY_BUT_IT_FAILED) else List.empty
    //List(SucceedOrFailEvent(successCheck, eventsIfTrue, eventsIfFalse, thisPokemon, otherPokemon))
    if(successCheck.apply()) eventsIfTrue else eventsIfFalse
  }
}

case class TrySeeded(probability: Double, displayFailure: Boolean, moveToAnimate: Option[Move])
  extends MoveEventGenerator {
  /** Returns a List containing an effect infliction event if successful; an empty list otherwise. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] = {
    val successCheck = () => !otherPokemon.getEffectTracker.contains(Seeded) &&
      Random.nextDouble() < probability
    val animationEvents = if (moveToAnimate.isEmpty) List.empty else List(PlayMoveAnimation(moveToAnimate.get))
    val eventsIfTrue = animationEvents ++ List(MoveEvent.getDisplayMessageSeeded(otherPokemon.getName),
      InflictEffectOnOpponent(Seeded))
    val eventsIfFalse = if(displayFailure) List(MoveEvent.DISPLAY_BUT_IT_FAILED) else List.empty
    //List(SucceedOrFailEvent(successCheck, eventsIfTrue, eventsIfFalse, thisPokemon, otherPokemon))
    if(successCheck.apply()) eventsIfTrue else eventsIfFalse
  }
}

case object TurnlyBurnDamage extends MoveEventGenerator {
  /** Deals 1/8th the current Pokemon's max HP. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] = {
    val result = ListBuffer.empty[MoveEvent]
    result.append(MoveEvent.getDisplayMessageHurtByBurn(thisPokemon.getName))
    result.append(PlayEffectAnimation(Burn))
    val damage = (thisPokemon.getStandardStats.getHP / 8.0).toInt //TODO magic numbers
    result.append(DealDamageToSelf(damage))
    if(MoveEventGenerator.willDamageKO(thisPokemon, damage)) result ++=
      MoveEventGenerator.getKOEvents(thisPokemon, false)
    result.toList
  }
}

case class TurnlyPoisonDamage(portionMaxHP: Double) extends MoveEventGenerator {
  /** Deals a portion of the current Pokemon's max HP. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] = {
    val result = ListBuffer.empty[MoveEvent]
    result.append(MoveEvent.getDisplayMessageHurtByPoison(thisPokemon.getName))
    val effect = thisPokemon.getEffectTracker.getPersistentEffect.get
    result.append(PlayEffectAnimation(effect))
    val damage = (thisPokemon.getStandardStats.getHP * portionMaxHP).toInt
    result.append(DealDamageToSelf(damage))
    result.append(WorsenPoisonSelf)
    if(MoveEventGenerator.willDamageKO(thisPokemon, damage)) result ++=
      MoveEventGenerator.getKOEvents(thisPokemon, false)
    result.toList
  }
}

case class TurnlySleep(turnsRemaining: Int) extends MoveEventGenerator {
  /** Decrements the sleep counter and sends a message if the Pokemon is fast asleep, or wakes up.
   *  In special cases, the Pokemon can still move. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] = {
    val result = ListBuffer.empty[MoveEvent]
    if(turnsRemaining == 0){
      result.append(MoveEvent.getDisplayMessageWokeUp(thisPokemon.getName))
      result.append(RemovePersistentEffectSelf)
    }
    else{
      result.append(MoveEvent.getDisplayMessageFastAsleep(thisPokemon.getName))
      val effect = thisPokemon.getEffectTracker.getPersistentEffect.get
      result.append(PlayEffectAnimation(effect))
      result.append(DecrementSleepCounterSelf)
      result.append(EndMove)
    }
    result.toList
  }
}

case object TurnlyParalysisCheck extends MoveEventGenerator {
  /** Checks if the pokemon can move. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] = {
    if(Random.nextDouble() < Paralyze.NO_MOVE_CHANCE)
      List(MoveEvent.getDisplayMessageFullyParalyzed(thisPokemon.getName), PlayEffectAnimation(Paralyze), EndMove)
    else
      List.empty
  }
}

case object TurnlyTryThaw extends MoveEventGenerator {
  /** Tries to thaw the Pokemon. On failure, prevents this Pokemon from moving because it is frozen. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] = {
    if(Random.nextDouble() < Frozen.THAW_CHANCE)
      List(MoveEvent.getDisplayMessageThawed(thisPokemon.getName), RemovePersistentEffectSelf)
    else
      List(MoveEvent.getDisplayMessageFrozenSolid(thisPokemon.getName), PlayEffectAnimation(Frozen), EndMove)
  }
}

case object ThawFrozenOther extends MoveEventGenerator {
  /** Thaws the other Pokemon. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] = {
    if(otherPokemon.getEffectTracker.getPersistentEffect.contains(Frozen))
      List(MoveEvent.getDisplayMessageThawed(otherPokemon.getName), RemovePersistentEffectOther)
    else
      List.empty
  }
}

case object TurnlySeededDamage extends MoveEventGenerator {
  /** Steals 1/8th the current Pokemon's max HP. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] = {
    val result = ListBuffer.empty[MoveEvent]
    result.append(PlayEffectAnimation(Seeded))
    val damage = (thisPokemon.getStandardStats.getHP / 8.0).toInt //TODO magic numbers
    result.append(DealDamageToSelf(damage))
    result.append(HealOther(damage))
    result.append(MoveEvent.getDisplayMessageHurtByLeechSeed(thisPokemon.getName))
    if(MoveEventGenerator.willDamageKO(thisPokemon, damage)) result ++=
      MoveEventGenerator.getKOEvents(thisPokemon, false)
    result.toList
  }
}
