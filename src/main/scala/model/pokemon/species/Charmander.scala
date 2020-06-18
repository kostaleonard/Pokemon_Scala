package model.pokemon.species

import model.elementaltype.{ElementalType, FireType}
import model.pokemon.Pokemon
import model.pokemon.exp.LevelTracker
import model.pokemon.move.{Move, MoveList}
import model.pokemon.move.bytype.normal.{Growl, ScaryFace, Scratch, Smokescreen}
import model.pokemon.move.bytype.fire.{Ember, Flamethrower}
import model.pokemon.move.bytype.steel.MetalClaw
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

class Charmander(override protected val levelTracker: LevelTracker)
  extends Pokemon(levelTracker) {

  /** Returns the Pokemon's pokedex number. */
  override def getPokedexNum: Int = 4

  /** Returns the Pokemon's pokedex entry. */
  def getPokedexEntry: String = "It has a preference for hot things. When it rains, steam is said to spout from the" +
    " tip of its tail."

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
    0 -> new Scratch,
    1 -> new Growl,
    7 -> new Ember,
    13 -> new MetalClaw,
    19 -> new Smokescreen,
    25 -> new ScaryFace,
    31 -> new Flamethrower
  )

  /** Returns the experience awarded for defeating this Pokemon. */
  override def getBaseExpAwarded: Int = 65
}
