package model.pokemon.exp

object LevelTracker {
  val MIN_LEVEL = 1
  val MAX_LEVEL = 100
  val EXPERIENCE_EXPONENT = 3.0

  /** Factory method. Returns a new instance of the class. */
  def create(level: Int): LevelTracker = new LevelTracker(level)
}

class LevelTracker(protected var level: Int) {
  if(level < LevelTracker.MIN_LEVEL || level > LevelTracker.MAX_LEVEL)
    throw new UnsupportedOperationException("Invalid level: %d".format(level))
  protected var currentExp = getExperienceForLevel(level)

  /** Returns the current level. */
  def getLevel: Int = level

  /** Returns the experience achieved at the current level. */
  def getExperienceAtCurrentLevel: Int = currentExp - getExperienceForLevel(level)

  /** Returns the experience required to attain a given level. */
  def getExperienceForLevel(lvl: Int): Int = math.pow(lvl.toDouble, LevelTracker.EXPERIENCE_EXPONENT).toInt

  /** Increments the amount of experience. It is the responsibility of the pokemon to determine if it has leveled up! */
  def gainExp(exp: Int): Unit = currentExp += exp

  /** Returns true if this object can level up. */
  def canLevelUp: Boolean = level < LevelTracker.MAX_LEVEL && currentExp >= getExperienceForLevel(level + 1)

  /** Increments the level. */
  def levelUp: Unit = level += 1
}
