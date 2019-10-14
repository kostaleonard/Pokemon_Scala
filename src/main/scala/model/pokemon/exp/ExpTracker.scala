package model.pokemon.exp

object ExpTracker {
  val MIN_LEVEL = 1
  val MAX_LEVEL = 100
}

class ExpTracker(private var level: Int) {
  if(level < ExpTracker.MIN_LEVEL || level > ExpTracker.MAX_LEVEL)
    throw new UnsupportedOperationException("Invalid level: %d".format(level))
  private var currentExp = getExperienceForLevel(level)

  /** Returns the current level. */
  def getLevel: Int = level

  /** Returns the experience achieved at the current level. */
  def getExperienceAtCurrentLevel: Int = currentExp - getExperienceForLevel(level)

  /** Returns the experience required to attain a given level. */
  def getExperienceForLevel(lvl: Int): Int = ??? //TODO experience for level.

  /** Increments the amount of experience. It is the responsibility of the pokemon to determine if it has leveled up! */
  def gainExp(exp: Int): Unit = currentExp += exp

  /** Returns true if this object can level up. */
  def canLevelUp: Boolean = level < ExpTracker.MAX_LEVEL && currentExp >= getExperienceForLevel(level + 1)

  /** Increments the level. */
  def levelUp: Unit = level += 1
}
