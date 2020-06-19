package model.pokemon.species

import model.elementaltype.{ElementalType, FireType}
import model.pokemon.Pokemon
import model.pokemon.exp.LevelTracker
import model.pokemon.move.bytype.dragon.DragonRage
import model.pokemon.move.{Move, MoveList}
import model.pokemon.move.bytype.normal._
import model.pokemon.move.bytype.fire.{Ember, FireSpin, Flamethrower}
import model.pokemon.move.bytype.steel.MetalClaw
import model.pokemon.stat.PokemonStats

object Charmeleon {
  val BASE_HP = 58
  val BASE_ATK = 64
  val BASE_DEF = 58
  val BASE_SPATK = 80
  val BASE_SPDEF = 65
  val BASE_SPD = 80

  val TYPE_ARRAY: Array[ElementalType] = Array(FireType)
}

class Charmeleon(override protected val levelTracker: LevelTracker)
  extends Pokemon(levelTracker) {

  /** Returns the Pokemon's pokedex number. */
  override def getPokedexNum: Int = 5

  /** Returns the Pokemon's pokedex entry. */
  def getPokedexEntry: String = "It lashes about with its tail to knock down its foe. It then tears up the fallen" +
    " opponent with sharp claws."

  /** Returns the name of the Pokemon species. */
  override def getSpeciesName: String = "CHARMELEON"

  /** Returns the base stats for the species. */
  override def getBaseStats: PokemonStats = new PokemonStats(scala.collection.immutable.Map[String, Int](
    PokemonStats.HP_KEY -> Charmeleon.BASE_HP,
    PokemonStats.ATK_KEY -> Charmeleon.BASE_ATK,
    PokemonStats.DEF_KEY -> Charmeleon.BASE_DEF,
    PokemonStats.SPATK_KEY -> Charmeleon.BASE_SPATK,
    PokemonStats.SPDEF_KEY -> Charmeleon.BASE_SPDEF,
    PokemonStats.SPD_KEY -> Charmeleon.BASE_SPD
  ))

  /** Returns the Pokemon's types. */
  override def getTypeArray: Array[ElementalType] = Charmeleon.TYPE_ARRAY

  /** Returns the Pokemon's learn map. */
  override def getLearnMap: Map[Int, Move] = Map(
    -1 -> new Scratch,
    0 -> new Growl,
    1 -> new Ember,
    7 -> new Ember,
    13 -> new MetalClaw,
    20 -> new Smokescreen,
    27 -> new ScaryFace,
    34 -> new Flamethrower,
    41 -> new Slash,
    48 -> new DragonRage,
    55 -> new FireSpin
  )

  /** Returns the experience awarded for defeating this Pokemon. */
  override def getBaseExpAwarded: Int = 142
}
