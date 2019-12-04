package model.pokemon.species

import model.elementaltype.{ElementalType, FireType, FlyingType}
import model.pokemon.Pokemon
import model.pokemon.exp.LevelTracker
import model.pokemon.move.{Move, MoveList}
import model.pokemon.move.bytype.normal.Scratch
import model.pokemon.move.bytype.fire.Ember
import model.pokemon.stat.PokemonStats

object Charizard {
  val BASE_HP = 78
  val BASE_ATK = 84
  val BASE_DEF = 78
  val BASE_SPATK = 109
  val BASE_SPDEF = 85
  val BASE_SPD = 100

  val TYPE_ARRAY: Array[ElementalType] = Array(FireType, FlyingType)
}

class Charizard(override protected val levelTracker: LevelTracker, override protected val wild: Boolean)
  extends Pokemon(levelTracker, wild) {

  /** Returns the Pokemon's pokedex number. */
  override def getPokedexNum: Int = 6

  /** Returns the Pokemon's pokedex entry. */
  def getPokedexEntry: String = "It spits fire that is hot enough to melt boulders. It may cause forest fires by " +
    "blowing flames."

  /** Returns the name of the Pokemon species. */
  override def getSpeciesName: String = "CHARIZARD"

  /** Returns the base stats for the species. */
  override def getBaseStats: PokemonStats = new PokemonStats(scala.collection.immutable.Map[String, Int](
    PokemonStats.HP_KEY -> Charizard.BASE_HP,
    PokemonStats.ATK_KEY -> Charizard.BASE_ATK,
    PokemonStats.DEF_KEY -> Charizard.BASE_DEF,
    PokemonStats.SPATK_KEY -> Charizard.BASE_SPATK,
    PokemonStats.SPDEF_KEY -> Charizard.BASE_SPDEF,
    PokemonStats.SPD_KEY -> Charizard.BASE_SPD
  ))

  /** Returns the Pokemon's types. */
  override def getTypeArray: Array[ElementalType] = Charizard.TYPE_ARRAY

  /** Returns the Pokemon's learn map. */
  override def getLearnMap: Map[Int, Move] = Map(
    //TODO correct/complete Charizard's learnset.
    1 -> new Scratch,
    7 -> new Ember
  )
}
