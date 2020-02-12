package model.pokemon.move.bytype.poison

import model.elementaltype.{ElementalType, PoisonType}
import model.pokemon.move._

object PoisonPowder {
  val BURN_CHANCE = 0.1
}

class PoisonPowder extends Move {
  /** Returns the name of the move, in all caps. */
  override def getName: String = "POISONPOWDER"

  /** Returns the initial value for the move's max PP. */
  override def getInitialMaxPP: Int = 35

  /** Returns the maximum value for the move's max PP. */
  override def getMaxMaxPP: Int = Move.getMaxMaxPP(getInitialMaxPP)

  /** Returns the move's power, or None if not applicable. */
  override def getPower: Option[Int] = None

  /** Returns the move's accuracy. */
  override def getAccuracy: Double = 0.75

  /** Returns the move's description. */
  override def getDescription: String = "Scatters a toxic powder that may poison the foe."

  /** Returns the path to the move's animation. */
  def getAnimationPath: String = "TODO"

  /** Returns the move's type. */
  override def getType: ElementalType = PoisonType

  /** Returns true if the move makes contact. */
  override def makesContact: Boolean = false

  /** Returns true if the move is physical. This influences whether the regular or special stats are used in the damage
    * calculation. */
  override def isPhysical: Boolean = false

  /** Returns the move's MoveActions in the order that they will be done. */
  override def getMoveActions: Array[MoveEventGenerator] =
    Array(AccuracyCheck(getAccuracy), TryPoison(1.0, true, false))
}

