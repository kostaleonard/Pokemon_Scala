package model.pokemon.move.bytype.normal

import model.elementaltype.{ElementalType, NormalType}
import model.pokemon.move._
import view.views.drawing.Animation

object Slash {
  val BASE_CRITICAL_HIT_MULTIPLIER = 2
}

class Slash extends Move {
  /** Returns the chance of a critical hit. Subclasses may override. */
  override def getCriticalHitChance: Double = Move.BASE_CRITICAL_HIT_CHANCE * Slash.BASE_CRITICAL_HIT_MULTIPLIER

  /** Returns the name of the move, in all caps. */
  override def getName: String = "SLASH"

  /** Returns the initial value for the move's max PP. */
  override def getInitialMaxPP: Int = 20

  /** Returns the maximum value for the move's max PP. */
  override def getMaxMaxPP: Int = Move.getMaxMaxPP(getInitialMaxPP)

  /** Returns the move's power, or None if not applicable. */
  override def getPower: Option[Int] = Some(70)

  /** Returns the move's accuracy. */
  override def getAccuracy: Double = 1.0

  /** Returns the move's description. */
  override def getDescription: String = "The foe is slashed with claws, etc. It has a high critical-hit ratio."

  /** Returns the move's animation from the player perspective. */
  override def getPlayerAnimation: Animation = Move.getPlaceholderPlayerAnimation

  /** Returns the move's animation from the opponent perspective. */
  override def getOpponentAnimation: Animation = Move.getPlaceholderOpponentAnimation

  /** Returns the move's type. */
  override def getType: ElementalType = NormalType

  /** Returns true if the move makes contact. */
  override def makesContact: Boolean = true

  /** Returns true if the move is physical. This influences whether the regular or special stats are used in the damage
    * calculation. */
  override def isPhysical: Boolean = true

  /** Returns the move's MoveActions in the order that they will be done. */
  override def getMoveActions: Array[MoveEventGenerator] = Array(AccuracyCheck(getAccuracy), MoveDamage(this))
}
