package model.pokemon

import model.pokemon.stat.PokemonStats

import scala.util.Random

object Pokemon {
  /** Returns a stats object representing a new Pokemon's IVs. */
  def getRandomIVStats: PokemonStats = {
    def getRandomIV: Int = Random.nextInt(15) + 1
    PokemonStats(getRandomIV, getRandomIV, getRandomIV, getRandomIV, getRandomIV, getRandomIV)
  }
}

abstract class Pokemon {
  private var ivStats = Pokemon.getRandomIVStats

  /** Returns the base stats for the species. */
  def getBaseStats: PokemonStats

  /** Returns the IV stats for this pokemon. */
  def getIVStats: PokemonStats = ivStats


}
