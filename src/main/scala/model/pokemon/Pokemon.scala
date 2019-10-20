package model.pokemon

import model.elementaltype.ElementalType
import model.pokemon.exp.LevelTracker
import model.pokemon.move.MoveList
import model.pokemon.stat.{BattleStats, IVStats, PokemonStats}

import scala.util.Random

object Pokemon {
  val NAME_MIN_LENGTH = 1
  val NAME_MAX_LENGTH = 15

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

abstract class Pokemon(protected val levelTracker: LevelTracker) {
  protected val ivStats: IVStats = Pokemon.getRandomIVStats
  protected var standardStats: PokemonStats = Pokemon.getStandardStats(getBaseStats, ivStats, getLevel)
  protected var currentStats = new BattleStats(standardStats)
  protected val moves: MoveList = getInitialMoveList(getLevel)
  protected var name: String = getSpeciesName

  /** Returns the Pokemon's name. By default, this is the species name. */
  def getName: String = name

  /** Changes the name to the new value. */
  def setName(newName: String): Unit =
    if(newName.length < Pokemon.NAME_MIN_LENGTH) throw new UnsupportedOperationException("Name too short.")
    else if(newName.length > Pokemon.NAME_MAX_LENGTH) throw new UnsupportedOperationException("Name too long.")
    else name = newName

  /** Returns the Pokemon's current level. */
  def getLevel: Int = levelTracker.getLevel

  /** Returns the IV stats for this pokemon. */
  def getIVStats: IVStats = ivStats

  /** Returns the stats the Pokemon would have after healing. */
  def getStandardStats: PokemonStats = standardStats

  /** Returns the stats the Pokemon currently has. */
  def getCurrentStats: PokemonStats = currentStats

  /** Decrements the current HP by the given amount. */
  def takeDamage(amount: Int): Unit = currentStats.takeDamage(amount)

  /** Returns the name of the Pokemon species. */
  def getSpeciesName: String

  /** Returns the base stats for the species. */
  def getBaseStats: PokemonStats

  /** Returns the Pokemon's types. */
  def getTypeArray: Array[ElementalType]

  /** Returns the Pokemon's moves at a given level. */
  def getInitialMoveList(level: Int): MoveList
}
