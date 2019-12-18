package model.party

import model.pokemon.Pokemon

import scala.collection.mutable.ListBuffer

object Party {
  val MAX_SIZE = 6
}

class Party(protected val pokemonList: ListBuffer[Pokemon]) {
  if(pokemonList.isEmpty) throw new UnsupportedOperationException("Cannot initialize Party object with no Pokemon.")
  else if(pokemonList.length > Party.MAX_SIZE) throw new UnsupportedOperationException(
    "Cannot initialize Party object with more than %d Pokemon.".format(Party.MAX_SIZE))

  /** Returns the first Pokemon that has not fainted, or None if no such Pokemon exists. */
  def getNextPokemon: Option[Pokemon] = pokemonList.collectFirst({case pok: Pokemon if !pok.isKO => pok})

  /** Returns the Pokemon list. */
  def getPokemonList: ListBuffer[Pokemon] = pokemonList

  /** Returns true if there is room for another Pokemon in the party. */
  def canAddPokemon: Boolean = pokemonList.length < Party.MAX_SIZE

  /** Returns true if there is room to remove a Pokemon from the party. */
  def canRemovePokemon: Boolean = pokemonList.length > 1

  /** Appends the Pokemon to the list. */
  def addPokemon(pokemon: Pokemon): Unit =
    if(canAddPokemon) pokemonList.append(pokemon)
    else throw new UnsupportedOperationException("Cannot add Pokemon to full party.")

  /** Removes the Pokemon at index from the list. */
  def removePokemon(index: Int): Unit =
    if(canRemovePokemon) pokemonList.remove(index)
    else throw new UnsupportedOperationException("Cannot remove only Pokemon from party.")
}
