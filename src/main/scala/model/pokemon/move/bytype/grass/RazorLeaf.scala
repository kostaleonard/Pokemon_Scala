package model.pokemon.move.bytype.grass

import model.elementaltype.{ElementalType, GrassType}
import model.pokemon.move.{AccuracyCheck, Move, MoveDamage, MoveEventGenerator}
import view.views.drawing.Animation

object RazorLeaf {
  val BASE_CRITICAL_HIT_MULTIPLIER = 2
}

class RazorLeaf extends Move {
  /** Returns the chance of a critical hit. Subclasses may override. */
  override def getCriticalHitChance: Double = Move.BASE_CRITICAL_HIT_CHANCE * RazorLeaf.BASE_CRITICAL_HIT_MULTIPLIER

  /** Returns the name of the move, in all caps. */
  override def getName: String = "RAZOR LEAF"

  /** Returns the initial value for the move's max PP. */
  override def getInitialMaxPP: Int = 25

  /** Returns the maximum value for the move's max PP. */
  override def getMaxMaxPP: Int = Move.getMaxMaxPP(getInitialMaxPP)

  /** Returns the move's power, or None if not applicable. */
  override def getPower: Option[Int] = Some(55)

  /** Returns the move's accuracy. */
  override def getAccuracy: Double = 0.95

  /** Returns the move's description. */
  override def getDescription: String = "Cuts the enemy with leaves. High critical-hit ratio."

  /** Returns the move's animation from the player perspective. */
  override def getPlayerAnimation: Animation = Move.getPlaceholderPlayerAnimation

  /** Returns the move's animation from the opponent perspective. */
  override def getOpponentAnimation: Animation = Move.getPlaceholderOpponentAnimation

  /** Returns the move's type. */
  override def getType: ElementalType = GrassType

  /** Returns true if the move makes contact. */
  override def makesContact: Boolean = false

  /** Returns true if the move is physical. This influences whether the regular or special stats are used in the damage
    * calculation. */
  override def isPhysical: Boolean = true

  /** Returns the move's MoveActions in the order that they will be done. */
  override def getMoveActions: Array[MoveEventGenerator] = Array(AccuracyCheck(getAccuracy), MoveDamage(this))
}
