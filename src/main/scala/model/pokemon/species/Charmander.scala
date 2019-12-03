package model.pokemon.species

import model.elementaltype.{ElementalType, FireType}
import model.pokemon.Pokemon
import model.pokemon.exp.LevelTracker
import model.pokemon.move.{Move, MoveList}
import model.pokemon.move.bytype.normal.Scratch
import model.pokemon.move.bytype.fire.Ember
import model.pokemon.stat.PokemonStats

object Charmander {
  val BASE_HP = 39
  val BASE_ATK = 52
  val BASE_DEF = 43
  val BASE_SPATK = 60
  val BASE_SPDEF = 50
  val BASE_SPD = 65

  val TYPE_ARRAY: Array[ElementalType] = Array(FireType)
}

class Charmander(override protected val levelTracker: LevelTracker, override protected val wild: Boolean)
  extends Pokemon(levelTracker, wild) {

  /** Returns the Pokemon's pokedex number. */
  override def getPokedexNum: Int = 4

  /** Returns the name of the Pokemon species. */
  override def getSpeciesName: String = "CHARMANDER"

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
  override def getTypeArray: Array[ElementalType] = Charmander.TYPE_ARRAY

  /** Returns the Pokemon's learn map. */
  override def getLearnMap: Map[Int, Move] = Map(
    //TODO correct/complete Charmander's learnset.
    1 -> new Scratch,
    7 -> new Ember
  )
}
