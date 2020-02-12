package model.pokemon.move

import model.pokemon.Pokemon
import model.pokemon.stat.BattleStats
import model.statuseffect.{Burn, Frozen, Poison, Sleep}

import scala.collection.mutable.ListBuffer
import scala.util.Random

object MoveEventGenerator {
  val FAINT_ANIMATION_PATH = "TODO Fainted"
  /** Returns true if the damage dealt will cause the pokemon to KO. */
  def willDamageKO(pokemon: Pokemon, damage: Int): Boolean = pokemon.getCurrentStats.getHP <= damage

  /** Returns the list of MoveEvents used when a Pokemon KOs. */
  def getKOEvents(pokemon: Pokemon, isOther: Boolean): List[MoveEvent] = List(PlayAnimation(FAINT_ANIMATION_PATH),
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
      ) * modifier).toInt

    result.append(PlayAnimation(move.getAnimationPath))
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

case class TryLowerStatOther(statKey: String, stages: Int) extends MoveEventGenerator {
  /** Lowers the opponent's given stat by the given number of stages. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] =
    if(otherPokemon.getCurrentStats.getStage(statKey) == BattleStats.MIN_STAGE)
      List(MoveEvent.getDisplayMessageStatWillNotGoLower(otherPokemon.getName, statKey))
    else {
      val result = ListBuffer.empty[MoveEvent]
      result.append(PlayAnimation("TODO"))
      result.append(LowerStatOther(statKey, stages))
      result.append(MoveEvent.getDisplayMessageStatFell(otherPokemon.getName, statKey))
      result.toList
    }
}

case class TryBurn(probability: Double, displayFailure: Boolean) extends MoveEventGenerator {
  /** Returns a List containing an effect infliction event if successful; an empty list otherwise. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] =
    if(otherPokemon.getEffectTracker.getPersistentEffects.isEmpty && Random.nextDouble() < probability)
      List(MoveEvent.getDisplayMessageBurned(otherPokemon.getName), InflictEffectOnOpponent(Burn))
    else if(displayFailure) List(MoveEvent.DISPLAY_BUT_IT_FAILED)
    else List.empty
}

case class TryPoison(probability: Double, displayFailure: Boolean, badly: Boolean) extends MoveEventGenerator {
  /** Returns a List containing an effect infliction event if successful; an empty list otherwise. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] =
    if(otherPokemon.getEffectTracker.getPersistentEffects.isEmpty && Random.nextDouble() < probability)
      List(MoveEvent.getDisplayMessagePoisoned(otherPokemon.getName), InflictEffectOnOpponent(Poison(badly, 1)))
    else if(displayFailure) List(MoveEvent.DISPLAY_BUT_IT_FAILED)
    else List.empty
}

case class TrySleep(probability: Double, displayFailure: Boolean, turns: Int) extends MoveEventGenerator {
  /** Returns a List containing an effect infliction event if successful; an empty list otherwise. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] =
    if(otherPokemon.getEffectTracker.getPersistentEffects.isEmpty && Random.nextDouble() < probability)
      List(MoveEvent.getDisplayMessageFellAsleep(otherPokemon.getName), InflictEffectOnOpponent(Sleep(turns)))
    else if(displayFailure) List(MoveEvent.DISPLAY_BUT_IT_FAILED)
    else List.empty
}

case object TurnlyBurnDamage extends MoveEventGenerator {
  /** Deals 1/8th the current Pokemon's max HP. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] = {
    val result = ListBuffer.empty[MoveEvent]
    result.append(MoveEvent.getDisplayMessageHurtByBurn(thisPokemon.getName))
    //TODO get burn animation.
    result.append(PlayAnimation("TODO"))
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
    //TODO get poison animation.
    result.append(PlayAnimation("TODO"))
    val damage = (thisPokemon.getStandardStats.getHP * portionMaxHP).toInt
    result.append(DealDamageToSelf(damage))
    result.append(WorsenPoisonSelf)
    if(MoveEventGenerator.willDamageKO(thisPokemon, damage)) result ++=
      MoveEventGenerator.getKOEvents(thisPokemon, false)
    result.toList
  }
}

case object TurnlySleep extends MoveEventGenerator {
  /** Decrements the sleep counter and sends a message if the Pokemon is fast asleep, or wakes up.
   *  In special cases, the Pokemon can still move. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] = {
    //TODO TurnlySleep
    val result = ListBuffer.empty[MoveEvent]
    //TODO try wake up
    result.append(DecrementSleepCounterSelf)
    //TODO more stuff

    result.toList
  }
}

case object TurnlyTryThaw extends MoveEventGenerator {
  /** Tries to thaw the Pokemon. On failure, prevents this Pokemon from moving because it is frozen. */
  override def getResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] = {
    if(Random.nextDouble() < Frozen.THAW_CHANCE)
      List(MoveEvent.getDisplayMessageThawed(thisPokemon.getName), RemoveFrozenSelf)
    else
      List(PlayAnimation("TODO"), MoveEvent.getDisplayMessageFrozenSolid(thisPokemon.getName), EndMove) //TODO get frozen animation.
  }
}
