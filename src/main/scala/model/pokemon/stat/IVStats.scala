package model.pokemon.stat

object IVStats {
  val MIN_VALUE = 1
  val MAX_VALUE = 15
}

class IVStats(private var hp: Int,
              private var attack: Int,
              private var defense: Int,
              private var specialAttack: Int,
              private var specialDefense: Int,
              private var speed: Int)
  extends PokemonStats(hp, attack, defense, specialAttack, specialDefense, speed) {

  /** Returns true if the given stat can be incremented by 1. */
  def canIncrementStat(statKey: String): Boolean = IVStats.MAX_VALUE > getStatsMap.getOrElse(statKey,
    throw new UnsupportedOperationException("No stat: %s".format(statKey)))

  /** Returns true if the given stat can be decremented by 1. */
  def canDecrementStat(statKey: String): Boolean = IVStats.MIN_VALUE < getStatsMap.getOrElse(statKey,
    throw new UnsupportedOperationException("No stat: %s".format(statKey)))

  /** Increments the specified stat. */
  def incrementStat(statKey: String): Unit = setStat(statKey,1 + getStatsMap.getOrElse(statKey,
    throw new UnsupportedOperationException("No stat: %s".format(statKey))))

  /** Decrements the specified stat. */
  def decrementStat(statKey: String): Unit = setStat(statKey,-1 + getStatsMap.getOrElse(statKey,
    throw new UnsupportedOperationException("No stat: %s".format(statKey))))

  /** Changes the value of a stat, but only if it falls within acceptable values. */
  override def setStat(statKey: String, newVal: Int): Unit =
    if(newVal >= IVStats.MIN_VALUE && newVal <= IVStats.MAX_VALUE) super.setStat(statKey, newVal)
    else throw new UnsupportedOperationException("Cannot set IV stat outside IV bounds.")
}
