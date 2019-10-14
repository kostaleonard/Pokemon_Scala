package model.pokemon

import model.elementaltype.ElementalType
import model.pokemon.exp.ExpTracker
import model.pokemon.move.{Move, MoveList}
import model.pokemon.stat.{IVStats, PokemonStats}

import scala.util.Random

object Pokemon {
  /** Returns a stats object representing a new Pokemon's IVs. */
  def getRandomIVStats: IVStats = {
    def getRandomIV: Int = Random.nextInt(IVStats.MAX_VALUE) + IVStats.MIN_VALUE
    new IVStats(getRandomIV, getRandomIV, getRandomIV, getRandomIV, getRandomIV, getRandomIV)
  }

  /** Returns the standard stats of this Pokemon. This is Leo's modified version, without EV stats. I don't like EVs. */
  def getStandardStats(baseStats: PokemonStats, ivStats: IVStats, level: Int): PokemonStats = {
    //TODO is this working how I want it? Can I clean this up?
    new PokemonStats(
      PokemonStats.SORTED_KEYS.map(key =>
        if (key == PokemonStats.HP_KEY) key -> (((baseStats.getHP + ivStats.getHP) * 2 * level) / 100 + level + 10)
        else key -> (((baseStats.getStat(key) + ivStats.getStat(key)) * 2 * level) / 100 + 5)
      ).toMap
    )
  }
}

abstract class Pokemon(private var level: Int) {
  private val ivStats = Pokemon.getRandomIVStats
  private var standardStats = Pokemon.getStandardStats(getBaseStats, ivStats, level)
  private var currentStats = standardStats.clone().asInstanceOf[PokemonStats] //TODO is this making a deep copy?
  private val expTracker = new ExpTracker(level)
  private var moves = List.empty[Move]

  /** Returns the IV stats for this pokemon. */
  def getIVStats: IVStats = ivStats

  /** Returns the base stats for the species. */
  def getBaseStats: PokemonStats

  /** Returns the Pokemon's types. */
  def getTypeArray: Array[ElementalType]

  /** Returns the Pokemon's moves at a given level. */
  def getInitialMoveList(level: Int): MoveList

  /** Returns the stats the Pokemon would have after healing. */
  def getStandardStats: PokemonStats = standardStats

  /** Returns the stats the Pokemon currently has. */
  def getCurrentStats: PokemonStats = currentStats

  /** Decrements the current HP by the given amount. */
  def takeDamage(amount: Int): Unit = currentStats.takeDamage(amount)
}
