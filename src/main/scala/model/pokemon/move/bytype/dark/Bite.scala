package model.pokemon.move.bytype.dark

import model.elementaltype.{ElementalType, DarkType}
import model.pokemon.move._
import view.views.drawing.Animation

object Bite {
  val FLINCH_CHANCE = 0.3
}

class Bite extends Move {
  /** Returns the name of the move, in all caps. */
  override def getName: String = "BITE"

  /** Returns the initial value for the move's max PP. */
  override def getInitialMaxPP: Int = 25

  /** Returns the maximum value for the move's max PP. */
  override def getMaxMaxPP: Int = Move.getMaxMaxPP(getInitialMaxPP)

  /** Returns the move's power, or None if not applicable. */
  override def getPower: Option[Int] = Some(60)

  /** Returns the move's accuracy. */
  override def getAccuracy: Double = 1.0

  /** Returns the move's description. */
  override def getDescription: String = "The user bites with vicious fangs. It may make the foe flinch."

  /** Returns the move's animation from the player perspective. */
  override def getPlayerAnimation: Animation = Move.getPlaceholderPlayerAnimation

  /** Returns the move's animation from the opponent perspective. */
  override def getOpponentAnimation: Animation = Move.getPlaceholderOpponentAnimation

  /** Returns the move's type. */
  override def getType: ElementalType = DarkType

  /** Returns true if the move makes contact. */
  override def makesContact: Boolean = true

  /** Returns true if the move is physical. This influences whether the regular or special stats are used in the damage
    * calculation. */
  override def isPhysical: Boolean = true

  /** Returns the move's MoveActions in the order that they will be done. */
  override def getMoveActions: Array[MoveEventGenerator] = Array(AccuracyCheck(getAccuracy), MoveDamage(this),
    TryFlinch(Bite.FLINCH_CHANCE, false, None))
}