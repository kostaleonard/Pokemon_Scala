package model.battle

import model.pokemon.Pokemon
import model.pokemon.move.MoveEvent

/** Stores the events, and who moved. */
case class MoveSpecification(moveEvent: MoveEvent, movingPokemon: Pokemon, otherPokemon: Pokemon)
