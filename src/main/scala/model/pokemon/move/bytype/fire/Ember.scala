package model.pokemon.move.bytype.fire

import model.elementaltype.{ElementalType, FireType}
import model.pokemon.move.{Damage, Move, MoveAction, TryBurn}

object Ember {
  val BURN_CHANCE = 0.1
}

class Ember extends Move {
  /** Returns the name of the move, in all caps. */
  override def getName: String = "EMBER"

  /** Returns the initial value for the move's max PP. */
  override def getInitialMaxPP: Int = 25

  /** Returns the maximum value for the move's max PP. */
  override def getMaxMaxPP: Int = Move.getMaxMaxPP(getInitialMaxPP)

  /** Returns the move's power, or None if not applicable. */
  override def getPower: Option[Int] = Some(40)

  /** Returns the move's accuracy. */
  override def getAccuracy: Double = 1.0

  /** Returns the move's description. */
  override def getDescription: String = "An attack that may inflict a burn."

  /** Returns the path to the move's animation. */
  def getAnimationPath: String = "TODO"

  /** Returns the move's type. */
  override def getType: ElementalType = FireType

  /** Returns true if the move makes contact. */
  override def makesContact: Boolean = false

  /** Returns true if the move is physical. This influences whether the regular or special stats are used in the damage
    * calculation. */
  override def isPhysical: Boolean = false

  /** Returns the move's MoveActions in the order that they will be done. */
  override def getMoveActions: Array[MoveAction] = Array(Damage(this), TryBurn(Ember.BURN_CHANCE))
}
