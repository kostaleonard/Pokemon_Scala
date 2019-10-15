package model.pokemon.move.bytype.normal

import model.elementaltype.{ElementalType, NormalType}
import model.pokemon.Pokemon
import model.pokemon.move.{Damage, Move, MoveAction}

class Tackle extends Move {
  /** Returns the initial value for the move's max PP. */
  override def getInitialMaxPP: Int = 35

  /** Returns the maximum value for the move's max PP. */
  override def getMaxMaxPP: Int = Move.getMaxMaxPP(getInitialMaxPP)

  /** Returns the move's power, or None if not applicable. */
  override def getPower: Option[Int] = Some(35)

  /** Returns the move's accuracy. */
  def getAccuracy: Double = 0.95

  /** Returns the move's description. */
  def getDescription: String = "Charges the foe with a full-body tackle."

  /** Returns the move's type. */
  def getType: ElementalType = NormalType

  /** Returns true if the move makes contact. */
  def makesContact: Boolean = true

  /** Returns the move's MoveEffects. */
  def getMoveActions: Array[MoveAction] = Array(Damage(getPower.get, getType))
}
