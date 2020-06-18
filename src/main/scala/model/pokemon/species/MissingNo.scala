package model.pokemon.species

import model.elementaltype.{ElementalType, FlyingType, GhostType, NormalType}
import model.pokemon.Pokemon
import model.pokemon.exp.LevelTracker
import model.pokemon.move.Move
import model.pokemon.move.bytype.dragon.DragonRage
import model.pokemon.move.bytype.fire.{BlessedImmolation, Ember, Flamethrower}
import model.pokemon.move.bytype.grass._
import model.pokemon.move.bytype.ice.AbsoluteZero
import model.pokemon.move.bytype.normal._
import model.pokemon.move.bytype.poison.{PoisonPowder, PoisonSpear}
import model.pokemon.move.bytype.steel.MetalClaw
import model.pokemon.stat.PokemonStats

object MissingNo {
  val BASE_HP = 255
  val BASE_ATK = 255
  val BASE_DEF = 255
  val BASE_SPATK = 255
  val BASE_SPDEF = 255
  val BASE_SPD = 255
  val DEFAULT_PERCENT_MALE = 0.0

  //TODO turn MissingNo back to Normal/Ghost
  val TYPE_ARRAY: Array[ElementalType] = Array(NormalType, FlyingType) // Array(NormalType, GhostType)
}

class MissingNo(override protected val levelTracker: LevelTracker)
  extends Pokemon(levelTracker) {

  /** Returns the Pokemon's pokedex number. */
  override def getPokedexNum: Int = 0

  /** Returns the Pokemon's pokedex entry. */
  def getPokedexEntry: String = "A Pokemon so powerful it can only be used for good or evil."

  /** Returns the name of the Pokemon species. */
  override def getSpeciesName: String = "MISSINGNO"

  /** Returns the percentage of this species that are male. Subclasses may override. */
  override def getPercentMale: Double = MissingNo.DEFAULT_PERCENT_MALE

  /** Returns the base stats for the species. */
  override def getBaseStats: PokemonStats = new PokemonStats(scala.collection.immutable.Map[String, Int](
    PokemonStats.HP_KEY -> MissingNo.BASE_HP,
    PokemonStats.ATK_KEY -> MissingNo.BASE_ATK,
    PokemonStats.DEF_KEY -> MissingNo.BASE_DEF,
    PokemonStats.SPATK_KEY -> MissingNo.BASE_SPATK,
    PokemonStats.SPDEF_KEY -> MissingNo.BASE_SPDEF,
    PokemonStats.SPD_KEY -> MissingNo.BASE_SPD
  ))

  /** Returns the Pokemon's types. */
  override def getTypeArray: Array[ElementalType] = MissingNo.TYPE_ARRAY

  /** Returns the Pokemon's learn map. */
  override def getLearnMap: Map[Int, Move] = Map(
    //TODO correct/complete MissingNo's learnset.
    -2 -> new Growth,
    -1 -> new Growl,
    0 -> new MetalClaw,
    1 -> new DragonRage
  )

  /** Returns the experience awarded for defeating this Pokemon. */
  override def getBaseExpAwarded: Int = 999
}
