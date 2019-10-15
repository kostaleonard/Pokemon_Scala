package model.pokemon.move

import model.pokemon.Pokemon

abstract class DamageMove extends Move {
  /** Does the move and returns the set of result codes from the Move object. */
  override def doAction(enemyPokemon: Pokemon): Set[Int] = {
    //TODO damage calculation.
    ???
  }
}
