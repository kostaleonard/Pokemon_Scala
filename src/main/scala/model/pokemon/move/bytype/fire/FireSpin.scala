package model.pokemon.move.bytype.fire

import model.elementaltype.{ElementalType, FireType}
import model.pokemon.move._
import view.views.drawing.Animation

import scala.util.Random

object FireSpin {
  val MIN_TURNS = 2
  val MAX_TURNS = 5
  val BURN_CHANCE = 0.1
}

class FireSpin extends Move {
  /** Returns the name of the move, in all caps. */
  override def getName: String = "FIRE SPIN"

  /** Returns the initial value for the move's max PP. */
  override def getInitialMaxPP: Int = 15

  /** Returns the maximum value for the move's max PP. */
  override def getMaxMaxPP: Int = Move.getMaxMaxPP(getInitialMaxPP)

  /** Returns the move's power, or None if not applicable. */
  override def getPower: Option[Int] = Some(35)

  /** Returns the move's accuracy. */
  override def getAccuracy: Double = 0.85

  /** Returns the move's description. */
  override def getDescription: String = "The foe is trapped in an intense spiral of fire that rages two to five turns."

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
    Array(AccuracyCheck(getAccuracy), TryFireVortex(this, 1.0,
      Random.nextInt(FireSpin.MAX_TURNS - FireSpin.MIN_TURNS) + FireSpin.MIN_TURNS, FireSpin.BURN_CHANCE, true))
}
