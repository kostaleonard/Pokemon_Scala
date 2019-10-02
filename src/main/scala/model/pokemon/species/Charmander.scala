package model.pokemon.species

import model.pokemon.Pokemon
import model.pokemon.stat.PokemonStats

class Charmander extends Pokemon {
  /** Returns the base stats for the species. */
  override def getBaseStats: PokemonStats = PokemonStats(-1, -1, -1, -1, -1, -1) //TODO fix Charmander's base stats.
}
