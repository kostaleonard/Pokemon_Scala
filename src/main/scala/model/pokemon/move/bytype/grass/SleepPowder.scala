package model.pokemon.move.bytype.grass

import model.elementaltype.{ElementalType, GrassType}
import model.pokemon.move._

import scala.util.Random

object SleepPowder {
  val MIN_TURNS = 2
  val MAX_TURNS = 5
}

class SleepPowder extends Move {
  /** Returns the name of the move, in all caps. */
  override def getName: String = "SLEEP POWDER"

  /** Returns the initial value for the move's max PP. */
  override def getInitialMaxPP: Int = 15

  /** Returns the maximum value for the move's max PP. */
  override def getMaxMaxPP: Int = Move.getMaxMaxPP(getInitialMaxPP)

  /** Returns the move's power, or None if not applicable. */
  override def getPower: Option[Int] = None

  /** Returns the move's accuracy. */
  override def getAccuracy: Double = 0.75

  /** Returns the move's description. */
  override def getDescription: String = "Scatters a powder that may cause the foe to sleep."

  /** Returns the path to the move's animation. */
  def getAnimationPath: String = "TODO"

  /** Returns the move's type. */
  override def getType: ElementalType = GrassType

  /** Returns true if the move makes contact. */
  override def makesContact: Boolean = false

  /** Returns true if the move is physical. This influences whether the regular or special stats are used in the damage
    * calculation. */
  override def isPhysical: Boolean = false

  /** Returns the move's MoveActions in the order that they will be done. */
  override def getMoveActions: Array[MoveEventGenerator] =
    Array(AccuracyCheck(getAccuracy), TrySleep(1.0, true,
      Random.nextInt(SleepPowder.MAX_TURNS - SleepPowder.MIN_TURNS) + SleepPowder.MIN_TURNS))
}

