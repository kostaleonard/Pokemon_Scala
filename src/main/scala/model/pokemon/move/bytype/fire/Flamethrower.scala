package model.pokemon.move.bytype.fire

import model.elementaltype.{ElementalType, FireType}
import model.pokemon.move._
import view.views.drawing.Animation

object Flamethrower {
  val BURN_CHANCE = 0.1
}

class Flamethrower extends Move {
  /** Returns the name of the move, in all caps. */
  override def getName: String = "FLAMETHROWER"

  /** Returns the initial value for the move's max PP. */
  override def getInitialMaxPP: Int = 15

  /** Returns the maximum value for the move's max PP. */
  override def getMaxMaxPP: Int = Move.getMaxMaxPP(getInitialMaxPP)

  /** Returns the move's power, or None if not applicable. */
  override def getPower: Option[Int] = Some(95)

  /** Returns the move's accuracy. */
  override def getAccuracy: Double = 1.0

  /** Returns the move's description. */
  override def getDescription: String = "The foe is scorched with intense flames. The foe may suffer a burn."

  /** Returns the move's animation from the player perspective. */
  override def getPlayerAnimation: Animation = Move.getPlaceholderPlayerAnimation

  /** Returns the move's animation from the opponent perspective. */
  override def getOpponentAnimation: Animation = Move.getPlaceholderPlayerAnimation

  /** Returns the move's type. */
  override def getType: ElementalType = FireType

  /** Returns true if the move makes contact. */
  override def makesContact: Boolean = false

  /** Returns true if the move is physical. This influences whether the regular or special stats are used in the damage
    * calculation. */
  override def isPhysical: Boolean = false

  /** Returns the move's MoveActions in the order that they will be done. */
  override def getMoveActions: Array[MoveEventGenerator] =
    Array(AccuracyCheck(getAccuracy), MoveDamage(this), TryBurn(Flamethrower.BURN_CHANCE, false, None), ThawFrozenOther)
}
