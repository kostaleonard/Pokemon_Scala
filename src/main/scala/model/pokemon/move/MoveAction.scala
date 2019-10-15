package model.pokemon.move

import model.elementaltype.ElementalType
import model.pokemon.Pokemon

sealed trait MoveAction {
  /** Do some action that occurs during a move (e.g. damage, effects, KO). */
  def doAction(thisPokemon: Pokemon, otherPokemon: Pokemon): Unit
}

case class Damage(movePower: Int, elementalType: ElementalType) extends MoveAction {
  /** Deals damage to the other pokemon. */
  override def doAction(thisPokemon: Pokemon, otherPokemon: Pokemon): Unit = {
    val damage = movePower
    //TODO actually calculate damage.
    otherPokemon.takeDamage(damage)
  }
}
