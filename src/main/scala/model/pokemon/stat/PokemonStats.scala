package model.pokemon.stat

object PokemonStats {
  val HP_KEY = "HP"
  val ATK_KEY = "ATK"
  val DEF_KEY = "DEF"
  val SPATK_KEY = "SPATK"
  val SPDEF_KEY = "SPDEF"
  val SPD_KEY = "SPD"
  val KEYS = Set(HP_KEY, ATK_KEY, DEF_KEY, SPATK_KEY, SPDEF_KEY, SPD_KEY)
  val SORTED_KEYS = Array(HP_KEY, ATK_KEY, DEF_KEY, SPATK_KEY, SPDEF_KEY, SPD_KEY)
}

class PokemonStats(protected var hp: Int,
                   protected var attack: Int,
                   protected var defense: Int,
                   protected var specialAttack: Int,
                   protected var specialDefense: Int,
                   protected var speed: Int) {
  /** Private Array constructor used by the Map constructor. Doesn't perform error checks, because the first line in the
    * constructor must be a call to another constructor. Use with extreme caution. */
  private def this(statsArray: Array[Int]) {
    this(statsArray(0), statsArray(1), statsArray(2), statsArray(3), statsArray(4), statsArray(5))
  }

  /** Map constructor. */
  def this(statsMap: Map[String, Int]) {
    this(PokemonStats.SORTED_KEYS.map(key =>
      statsMap.getOrElse(key, throw new UnsupportedOperationException("Stat not found: %s".format(key)))))
  }

  /** Returns all the stats as a Map. */
  def getStatsMap: Map[String, Int] = scala.collection.immutable.Map[String, Int](
    PokemonStats.HP_KEY -> hp,
    PokemonStats.ATK_KEY -> attack,
    PokemonStats.DEF_KEY -> defense,
    PokemonStats.SPATK_KEY -> specialAttack,
    PokemonStats.SPDEF_KEY -> specialDefense,
    PokemonStats.SPD_KEY -> speed
  )

  /** Returns the requested stat. Keys are defined in PokemonStats object. */
  def getStat(statKey: String): Int = getStatsMap.getOrElse(statKey,
    throw new UnsupportedOperationException("Stat not found: %s".format(statKey)))

  /** Returns the Pokemon's HP. */
  def getHP: Int = getStat(PokemonStats.HP_KEY)

  /** Returns the Pokemon's Attack. */
  def getAttack: Int = getStat(PokemonStats.ATK_KEY)

  /** Returns the Pokemon's Defense. */
  def getDefense: Int = getStat(PokemonStats.DEF_KEY)

  /** Returns the Pokemon's Special Attack. */
  def getSpecialAttack: Int = getStat(PokemonStats.SPATK_KEY)

  /** Returns the Pokemon's Special Defense. */
  def getSpecialDefense: Int = getStat(PokemonStats.SPDEF_KEY)

  /** Returns the Pokemon's Speed. */
  def getSpeed: Int = getStat(PokemonStats.SPD_KEY)

  /** Sets the given stat to a new value. */
  def setStat(statKey: String, newVal: Int): Unit =
    if(newVal < 0) throw new UnsupportedOperationException("Cannot set stat below 0.")
    else statKey match{
      case PokemonStats.HP_KEY => hp = newVal
      case PokemonStats.ATK_KEY => attack = newVal
      case PokemonStats.DEF_KEY => defense = newVal
      case PokemonStats.SPATK_KEY => specialAttack = newVal
      case PokemonStats.SPDEF_KEY => specialDefense = newVal
      case PokemonStats.SPD_KEY => speed = newVal
      case _ => throw new UnsupportedOperationException("Stat not found: %s".format(statKey))
  }

  /** Sets the HP to a new value. */
  def setHP(newVal: Int): Unit = setStat(PokemonStats.HP_KEY, newVal)

  /** Sets the Attack to a new value. */
  def setAttack(newVal: Int): Unit = setStat(PokemonStats.ATK_KEY, newVal)

  /** Sets the Defense to a new value. */
  def setDefense(newVal: Int): Unit = setStat(PokemonStats.DEF_KEY, newVal)

  /** Sets the Special Attack to a new value. */
  def setSpecialAttack(newVal: Int): Unit = setStat(PokemonStats.SPATK_KEY, newVal)

  /** Sets the Special Defense to a new value. */
  def setSpecialDefense(newVal: Int): Unit = setStat(PokemonStats.SPDEF_KEY, newVal)

  /** Sets the Speed to a new value. */
  def setSpeed(newVal: Int): Unit = setStat(PokemonStats.SPD_KEY, newVal)

  /** Returns the string representation of the object. */
  override def toString: String = "HP = %d\nATK = %d\nDEF = %d\nSPATK = %d\nSPDEF = %d\nSPD = %d".format(
    getHP, getAttack, getDefense, getSpecialAttack, getSpecialDefense, getSpeed)
}
