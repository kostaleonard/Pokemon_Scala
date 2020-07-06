package model.pokemon.species

import model.elementaltype.{AbnormalType, ElementalType, FlyingType, NormalType}
import model.pokemon.Pokemon
import model.pokemon.exp.LevelTracker
import model.pokemon.move.Move
import model.pokemon.move.bytype.dark.Bite
import model.pokemon.move.bytype.flying.WingAttack
import model.pokemon.move.bytype.normal.{Growl, Growth}
import model.pokemon.stat.PokemonStats

object MissingNo2 {
  val BASE_HP = 255
  val BASE_ATK = 255
  val BASE_DEF = 255
  val BASE_SPATK = 255
  val BASE_SPDEF = 255
  val BASE_SPD = 255
  val DEFAULT_PERCENT_MALE = 1.0

  val TYPE_ARRAY: Array[ElementalType] = Array(AbnormalType)
}

class MissingNo2(override protected val levelTracker: LevelTracker)
  extends Pokemon(levelTracker) {

  /** Returns the Pokemon's pokedex number. */
  override def getPokedexNum: Int = -1

  /** Returns the Pokemon's pokedex entry. */
  def getPokedexEntry: String = "The only known Abnormal type Pokemon."

  /** Returns the name of the Pokemon species. */
  override def getSpeciesName: String = "MISSINGNO2"

  /** Returns the percentage of this species that are male. Subclasses may override. */
  override def getPercentMale: Double = MissingNo.DEFAULT_PERCENT_MALE

  /** Returns the base stats for the species. */
  override def getBaseStats: PokemonStats = new PokemonStats(scala.collection.immutable.Map[String, Int](
    PokemonStats.HP_KEY -> MissingNo2.BASE_HP,
    PokemonStats.ATK_KEY -> MissingNo2.BASE_ATK,
    PokemonStats.DEF_KEY -> MissingNo2.BASE_DEF,
    PokemonStats.SPATK_KEY -> MissingNo2.BASE_SPATK,
    PokemonStats.SPDEF_KEY -> MissingNo2.BASE_SPDEF,
    PokemonStats.SPD_KEY -> MissingNo2.BASE_SPD
  ))

  /** Returns the Pokemon's types. */
  override def getTypeArray: Array[ElementalType] = MissingNo.TYPE_ARRAY

  /** Returns the Pokemon's learn map. */
  override def getLearnMap: Map[Int, Move] = Map(
    //TODO correct/complete MissingNo2's learnset.
    -2 -> new Bite
  )

  /** Returns the experience awarded for defeating this Pokemon. */
  override def getBaseExpAwarded: Int = 999
}