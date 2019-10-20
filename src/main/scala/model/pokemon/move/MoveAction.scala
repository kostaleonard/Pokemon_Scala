package model.pokemon.move

import model.pokemon.Pokemon

import scala.collection.mutable.ListBuffer
import scala.util.Random

sealed trait MoveAction {
  /** Does some action that occurs during a move (e.g. damage, effects, KO). Returns the List of MoveEvents that result
    * from this action. */
  def calculateResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent]
}

case class Damage(move: Move) extends MoveAction {
  /** Deals damage to the other pokemon. */
  override def calculateResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] = {
    if(Random.nextDouble() > move.getAccuracy) return List(MoveEvent.getDisplayMessageMoveMissed(thisPokemon.getName))

    var result = ListBuffer.empty[MoveEvent]

    val targets = 1 //TODO if multiple targets, this is 0.75.
    val weather = 1 //TODO 1.5 if water move in rain or fire move in harsh sunlight; 0.5 if water move in harsh sunlight or fire move in rain.
    val isCriticalHit = Random.nextDouble() < move.getCriticalHitChance
    var critical = if(isCriticalHit) 2 else 1
    val randomFactor = Random.nextDouble() * 0.15 + 0.85
    val stab = if(thisPokemon.getTypeArray.contains(move.getType)) 1.5 else 1
    val typeEffectiveness = otherPokemon.getTypeArray.foldRight(1.0)((otherType, accum) =>
      accum * move.getType.getTypeEffectiveness(otherType))
    val burned = 1 //TODO if burned and move is physical, this is 0.5.
    val other = 1 //TODO used in some moves.

    val modifier = targets * weather * critical * randomFactor * stab * typeEffectiveness * burned

    val A = if(move.isPhysical) thisPokemon.getCurrentStats.getAttack
      else thisPokemon.getCurrentStats.getSpecialAttack
    val D = if(move.isPhysical) otherPokemon.getCurrentStats.getDefense
      else otherPokemon.getCurrentStats.getSpecialDefense

    val damage = ((
      ((2.0 * thisPokemon.getLevel) / 5.0 * move.getPower.get * (A.toDouble / D.toDouble)) / 50.0 + 2.0
      ) * modifier).toInt

    if(isCriticalHit) result.append(MoveEvent.DISPLAY_CRITICAL_HIT)
    result.append(DealDamageToOpponent(damage))
    if(typeEffectiveness == 0) result.append(
      MoveEvent.getDisplayMessageMoveNoEffect(move.getName, otherPokemon.getName))
    else if(typeEffectiveness < 1) result.append(MoveEvent.DISPLAY_NOT_VERY_EFFECTIVE)
    else if(typeEffectiveness > 1) result.append(MoveEvent.DISPLAY_SUPER_EFFECTIVE)
    result.toList
  }
}
