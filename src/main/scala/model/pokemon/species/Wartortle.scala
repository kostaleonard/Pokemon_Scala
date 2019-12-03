package model.pokemon.species

import model.elementaltype.{ElementalType, WaterType}
import model.pokemon.Pokemon
import model.pokemon.exp.LevelTracker
import model.pokemon.move.{Move, MoveList}
import model.pokemon.stat.PokemonStats

object Wartortle {
  val BASE_HP = 59
  val BASE_ATK = 63
  val BASE_DEF = 80
  val BASE_SPATK = 65
  val BASE_SPDEF = 80
  val BASE_SPD = 58

  val TYPE_ARRAY: Array[ElementalType] = Array(WaterType)
}

class Wartortle(override protected val levelTracker: LevelTracker, override protected val wild: Boolean)
  extends Pokemon(levelTracker, wild) {

  /** Returns the Pokemon's pokedex number. */
  override def getPokedexNum: Int = 8

  /** Returns the name of the Pokemon species. */
  override def getSpeciesName: String = "WARTORTLE"

  /** Returns the base stats for the species. */
  override def getBaseStats: PokemonStats = new PokemonStats(scala.collection.immutable.Map[String, Int](
    PokemonStats.HP_KEY -> Charmander.BASE_HP,
    PokemonStats.ATK_KEY -> Charmander.BASE_ATK,
    PokemonStats.DEF_KEY -> Charmander.BASE_DEF,
    PokemonStats.SPATK_KEY -> Charmander.BASE_SPATK,
    PokemonStats.SPDEF_KEY -> Charmander.BASE_SPDEF,
    PokemonStats.SPD_KEY -> Charmander.BASE_SPD
  ))

  /** Returns the Pokemon's types. */
  override def getTypeArray: Array[ElementalType] = Wartortle.TYPE_ARRAY

  /** Returns the Pokemon's learn map. */
  override def getLearnMap: Map[Int, Move] = Map(
    //TODO correct/complete learnset.
    1 -> new Tackle
  )
}
