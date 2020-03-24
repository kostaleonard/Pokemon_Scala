package model.pokemon.stat

object BattleStats {
  val MIN_STAGE: Int = -6
  val MAX_STAGE: Int = 6
  val STAT_STAGE_MULTIPLIERS: scala.collection.immutable.Map[Int, Double] = scala.collection.immutable.Map[Int, Double](
    -6 -> 2.0 / 8.0,
    -5 -> 2.0 / 7.0,
    -4 -> 2.0 / 6.0,
    -3 -> 2.0 / 5.0,
    -2 -> 2.0 / 4.0,
    -1 -> 2.0 / 3.0,
    0 -> 2.0 / 2.0,
    1 -> 3.0 / 2.0,
    2 -> 4.0 / 2.0,
    3 -> 5.0 / 2.0,
    4 -> 6.0 / 2.0,
    5 -> 7.0 / 2.0,
    6 -> 8.0 / 2.0
  )
}

/** Constructors here are reproduced from the super class because the subclass cannot call super's auxiliary
  * constructors. Not nice. */
class BattleStats(protected val baselineStats: PokemonStats) extends PokemonStats(baselineStats.getStatsMap) {
  private val statStages = scala.collection.mutable.Map[String, Int](
    PokemonStats.ATK_KEY -> 0,
    PokemonStats.DEF_KEY -> 0,
    PokemonStats.SPATK_KEY -> 0,
    PokemonStats.SPDEF_KEY -> 0,
    PokemonStats.SPD_KEY -> 0
  )
  private var isParalyzed: Boolean = false
  private var isBurned: Boolean = false

  /** Returns true if the Pokemon is KO, false otherwise. */
  def isKO: Boolean = getStat(PokemonStats.HP_KEY) == 0

  /** Lowers HP by the given amount. */
  def takeDamage(amount: Int): Unit = setStat(PokemonStats.HP_KEY, (getStat(PokemonStats.HP_KEY) - amount) max 0)

  /** Increases HP by the given amount. */
  def healDamage(amount: Int): Unit = setStat(PokemonStats.HP_KEY, (getStat(PokemonStats.HP_KEY) + amount)
    min baselineStats.getHP)

  /** Returns the stage of the given staged stat. */
  def getStage(statKey: String): Int = statStages.getOrElse(statKey,
    throw new UnsupportedOperationException("No such staged stat: %s".format(statKey)))

  /** Changes the stage of the stat by the given increment, which can be negative. */
  def setStage(statKey: String, newStage: Int): Unit = {
    if(statStages.get(statKey).isEmpty)
      throw new UnsupportedOperationException("No such staged stat: %s".format(statKey))
    val statMultiplier = BattleStats.STAT_STAGE_MULTIPLIERS.getOrElse(newStage,
      throw new UnsupportedOperationException("No stat stage: %d".format(newStage)))
    statStages(statKey) = newStage
    setStat(statKey, (baselineStats.getStat(statKey) * statMultiplier).toInt)
  }

  /** Increments the stage of the stat. */
  def incrementStage(statKey: String, increment: Int): Unit = setStage(statKey, getStage(statKey) + increment)

  /** Resets all stages, but doesn't change the actual stats. Used during resets because all stats will be set to the
    * baseline independently. Could call setStage, but this is just faster. */
  protected def resetStagesNoStatUpdate(): Unit = statStages.keys.foreach(statStages(_) = 0)

  /** Returns all the stats as a Map. Checks status conditions. */
  override def getStatsMap: Map[String, Int] =
    if(isParalyzed) super.getStatsMap.updated(PokemonStats.SPD_KEY, super.getStatsMap(PokemonStats.SPD_KEY) / 2)
    else if(isBurned) super.getStatsMap.updated(PokemonStats.ATK_KEY, super.getStatsMap(PokemonStats.ATK_KEY) / 2)
    else super.getStatsMap

  /** Sets the stats of this pokemon to reflect paralyzed status. */
  def setParalyzed(b: Boolean): Unit = isParalyzed = b

  /** Sets the stats of this pokemon to reflect burned status. */
  def setBurned(b: Boolean): Unit = isBurned = b

  /** Resets all stats to their baseline levels and resets all stages. This is what gets called when you heal at a
    * Pokemon Center. */
  def resetOnHeal(): Unit = {
    resetStagesNoStatUpdate()
    baselineStats.getStatsMap.foreach(pair => setStat(pair._1, pair._2))
    isParalyzed = false
    isBurned = false
  }

  /** Resets all stats except HP to their baseline levels and resets all stat stages. This is what gets called when you
    * switch out a Pokemon. */
  def resetOnSwitch(): Unit = {
    resetStagesNoStatUpdate()
    baselineStats.getStatsMap.filter(_._1 != PokemonStats.HP_KEY).foreach(pair => setStat(pair._1, pair._2))
  }

  /** Resets all stats except HP to their baseline levels and resets all stat stages. This is what gets called when you
    * leave battle. Identical to resetOnSwitch. */
  def resetAfterBattle(): Unit = resetOnSwitch()
}
