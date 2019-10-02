package model.party

import model.pokemon.Pokemon

class Party(pokemonList: List[Pokemon]) {
  if(pokemonList.isEmpty) throw new UnsupportedOperationException("Cannot initialize Party object with no Pokemon.")
}
