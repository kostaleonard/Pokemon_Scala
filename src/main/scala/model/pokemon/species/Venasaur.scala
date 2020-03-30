package model.pokemon.species

import model.elementaltype.{ElementalType, GrassType, PoisonType}
import model.pokemon.Pokemon
import model.pokemon.exp.LevelTracker
import model.pokemon.move.bytype.grass._
import model.pokemon.move.bytype.normal.{Growl, Growth, Tackle}
import model.pokemon.move.bytype.poison.{PoisonPowder, PoisonSpear}
import model.pokemon.move.{Move, MoveList}
import model.pokemon.stat.PokemonStats

object Venasaur {
  val BASE_HP = 80
  val BASE_ATK = 82
  val BASE_DEF = 83
  val BASE_SPATK = 100
  val BASE_SPDEF = 100
  val BASE_SPD = 80

  val TYPE_ARRAY: Array[ElementalType] = Array(GrassType, PoisonType)
}

class Venasaur(override protected val levelTracker: LevelTracker)
  extends Pokemon(levelTracker) {

  /** Returns the Pokemon's pokedex number. */
  override def getPokedexNum: Int = 3

  /** Returns the Pokemon's pokedex entry. */
  def getPokedexEntry: String = "The plant blooms when it is absorbing solar energy. It stays on the move to seek" +
    " sunlight."

  /** Returns the name of the Pokemon species. */
  override def getSpeciesName: String = "VENASAUR"

  /** Returns the base stats for the species. */
  override def getBaseStats: PokemonStats = new PokemonStats(scala.collection.immutable.Map[String, Int](
    PokemonStats.HP_KEY -> Venasaur.BASE_HP,
    PokemonStats.ATK_KEY -> Venasaur.BASE_ATK,
    PokemonStats.DEF_KEY -> Venasaur.BASE_DEF,
    PokemonStats.SPATK_KEY -> Venasaur.BASE_SPATK,
    PokemonStats.SPDEF_KEY -> Venasaur.BASE_SPDEF,
    PokemonStats.SPD_KEY -> Venasaur.BASE_SPD
  ))

  /** Returns the Pokemon's types. */
  override def getTypeArray: Array[ElementalType] = Venasaur.TYPE_ARRAY

  /** Returns the Pokemon's learn map. */
  override def getLearnMap: Map[Int, Move] = Map(
    -2 -> new Tackle,
    -1 -> new Growl,
    0 -> new LeechSeed,
    1 -> new VineWhip,
    4 -> new Growl,
    7 -> new LeechSeed,
    10 -> new VineWhip,
    14 -> new PoisonPowder,
    15 -> new SleepPowder,
    22 -> new RazorLeaf,
    29 -> new PoisonSpear,
    41 -> new Growth,
    53 -> new Synthesis,
    65 -> new SolarBeam
  )

  /** Returns the experience awarded for defeating this Pokemon. */
  override def getBaseExpAwarded: Int = 208
}
