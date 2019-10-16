package model.pokemon.move

import model.elementaltype.ElementalType
import model.pokemon.Pokemon

sealed trait MoveAction {
  /** Does some action that occurs during a move (e.g. damage, effects, KO). Returns the List of MoveEvents that result
    * from this action. */
  def calculateResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent]
}

case class Damage(power: Int, elementalType: ElementalType) extends MoveAction {
  /** Deals damage to the other pokemon. */
  override def calculateResults(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEvent] = {
    val damage = power
    //TODO actually calculate damage.
    List(DealDamageToOpponent(damage))
  }
}
