package model.pokemon.species

import model.elementaltype.{ElementalType, WaterType}
import model.pokemon.Pokemon
import model.pokemon.exp.LevelTracker
import model.pokemon.move.bytype.normal.Tackle
import model.pokemon.move.{Move, MoveList}
import model.pokemon.stat.PokemonStats

object Squirtle {
  val BASE_HP = 44
  val BASE_ATK = 48
  val BASE_DEF = 65
  val BASE_SPATK = 50
  val BASE_SPDEF = 64
  val BASE_SPD = 43

  val TYPE_ARRAY: Array[ElementalType] = Array(WaterType)
}

class Squirtle(override protected val levelTracker: LevelTracker)
  extends Pokemon(levelTracker) {

  /** Returns the Pokemon's pokedex number. */
  override def getPokedexNum: Int = 7

  /** Returns the Pokemon's pokedex entry. */
  def getPokedexEntry: String = "After birth, its back swells and hardens into a shell. Powerfully sprays foam from" +
    " its mouth."

  /** Returns the name of the Pokemon species. */
  override def getSpeciesName: String = "SQUIRTLE"

  /** Returns the base stats for the species. */
  override def getBaseStats: PokemonStats = new PokemonStats(scala.collection.immutable.Map[String, Int](
    PokemonStats.HP_KEY -> Squirtle.BASE_HP,
    PokemonStats.ATK_KEY -> Squirtle.BASE_ATK,
    PokemonStats.DEF_KEY -> Squirtle.BASE_DEF,
    PokemonStats.SPATK_KEY -> Squirtle.BASE_SPATK,
    PokemonStats.SPDEF_KEY -> Squirtle.BASE_SPDEF,
    PokemonStats.SPD_KEY -> Squirtle.BASE_SPD
  ))

  /** Returns the Pokemon's types. */
  override def getTypeArray: Array[ElementalType] = Squirtle.TYPE_ARRAY

  /** Returns the Pokemon's learn map. */
  override def getLearnMap: Map[Int, Move] = Map(
    //TODO correct/complete learnset.
    1 -> new Tackle
  )
}
