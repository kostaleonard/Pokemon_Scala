package model.pokemon.species

import model.elementaltype.{ElementalType, FireType, FlyingType}
import model.pokemon.Pokemon
import model.pokemon.exp.LevelTracker
import model.pokemon.move.bytype.dragon.DragonRage
import model.pokemon.move.{Move, MoveList}
import model.pokemon.move.bytype.normal._
import model.pokemon.move.bytype.fire.{Ember, FireSpin, Flamethrower}
import model.pokemon.move.bytype.flying.WingAttack
import model.pokemon.move.bytype.steel.MetalClaw
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

class Charizard(override protected val levelTracker: LevelTracker)
  extends Pokemon(levelTracker) {

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
    -2 -> new Scratch,
    -1 -> new Growl,
    0 -> new Ember,
    1 -> new MetalClaw,
    7 -> new Ember,
    13 -> new MetalClaw,
    20 -> new Smokescreen,
    27 -> new ScaryFace,
    34 -> new Flamethrower,
    36 -> new WingAttack,
    44 -> new Slash,
    54 -> new DragonRage,
    64 -> new FireSpin
  )

  /** Returns the experience awarded for defeating this Pokemon. */
  override def getBaseExpAwarded: Int = 209
}
