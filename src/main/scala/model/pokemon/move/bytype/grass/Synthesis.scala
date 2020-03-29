package model.pokemon.move.bytype.grass

import model.elementaltype.{ElementalType, GrassType}
import model.pokemon.move._
import view.views.drawing.Animation

object Synthesis {
  val HEAL_PERCENT_SUNNY = 0.67
  val HEAL_PERCENT_NORMAL = 0.5
  val HEAL_PERCENT_BAD_WEATHER = 0.25
}

class Synthesis extends Move {
  /** Returns the name of the move, in all caps. */
  override def getName: String = "SYNTHESIS"

  /** Returns the initial value for the move's max PP. */
  override def getInitialMaxPP: Int = 5

  /** Returns the maximum value for the move's max PP. */
  override def getMaxMaxPP: Int = Move.getMaxMaxPP(getInitialMaxPP)

  /** Returns the move's power, or None if not applicable. */
  override def getPower: Option[Int] = None

  /** Returns the move's accuracy. */
  override def getAccuracy: Double = 1.0

  /** Returns the move's description. */
  override def getDescription: String = "Restores HP. The amount varies with the weather."

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
  override def isPhysical: Boolean = false

  //TODO weather.
  /** Returns the move's MoveActions in the order that they will be done. */
  override def getMoveActions: Array[MoveEventGenerator] =
    Array(TryHealSelf(Synthesis.HEAL_PERCENT_NORMAL, Some(this)))
}
