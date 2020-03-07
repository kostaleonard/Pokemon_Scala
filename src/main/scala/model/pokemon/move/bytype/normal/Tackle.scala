package model.pokemon.move.bytype.normal

import model.elementaltype.{ElementalType, NormalType}
import model.pokemon.move.{AccuracyCheck, Move, MoveDamage, MoveEventGenerator}
import view.views.drawing.Animation

class Tackle extends Move {
  /** Returns the name of the move, in all caps. */
  override def getName: String = "TACKLE"

  /** Returns the initial value for the move's max PP. */
  override def getInitialMaxPP: Int = 35

  /** Returns the maximum value for the move's max PP. */
  override def getMaxMaxPP: Int = Move.getMaxMaxPP(getInitialMaxPP)

  /** Returns the move's power, or None if not applicable. */
  override def getPower: Option[Int] = Some(35)

  /** Returns the move's accuracy. */
  override def getAccuracy: Double = 0.95

  /** Returns the move's description. */
  override def getDescription: String = "Charges the foe with a full-body tackle."

  /** Returns the move's animation. */
  override def getAnimation: Animation = Move.getPlaceholderAnimation

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
