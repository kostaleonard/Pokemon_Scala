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
    var result = ListBuffer.empty[MoveEvent]

    val targets = 1 //TODO if multiple targets, this is 0.75.
    val weather = 1 //TODO 1.5 if water move in rain or fire move in harsh sunlight; 0.5 if water move in harsh sunlight or fire move in rain.
    val isCriticalHit = Random.nextDouble() < move.getCriticalHitChance
    var critical = if(isCriticalHit) 2 else 1
    val randomFactor = Random.nextDouble() * 0.15 + 0.85
    val stab = if(thisPokemon.getTypeArray.contains(move.getType)) 1.5 else 1
    val typeEffectiveness = 1 //TODO type effectiveness
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

    if(isCriticalHit) result.append(DisplayCriticalHit)
    result.append(DealDamageToOpponent(damage))
    if(typeEffectiveness == 0) result.append(DisplayMoveDoesNotAffect(move.getName, otherPokemon.getName))
    else if(typeEffectiveness < 1) result.append(DisplayNotVeryEffective)
    else if(typeEffectiveness > 1) result.append(DisplaySuperEffective)
    result.toList
  }
}
