package model.pokemon.move

import model.elementaltype.ElementalType
import model.pokemon.Pokemon

object Move {
  /** Move result codes. */
  val HIT = 0
  val MISS = 1
  val FAILED = 2
  val ENEMY_FAINT = 3
  val SELF_FAINT = 4
  val MUST_RECHARGE = 5
  //TODO [stat] won't go higher/lower?
  //TODO [pokemon] was poisoned/paralyzed/etc.

  /** Returns standard max max PP for a given max PP. These are more like PP guidelines than laws. */
  def getMaxMaxPP(maxPP: Int): Int = maxPP * 8 / 5
}

abstract class Move {
  protected var maxPP = getInitialMaxPP
  protected var currentPP = getMaxPP

  /** Returns the current move PP. */
  def getCurrentPP: Int = currentPP

  /** Returns the current move's Max PP. This can change, up to the MaxMaxPP. */
  def getMaxPP: Int = maxPP

  /** Sets the current PP to the max PP. */
  def replenishPP: Unit = currentPP = maxPP

  /** Changes the maximum PP for this move. */
  def setMaxPP(value: Int): Unit =
    if(value < 1 || value > getMaxMaxPP)
      throw new UnsupportedOperationException("Max PP must be between 1 and %d.".format(getMaxMaxPP))
    else {
      maxPP = value
      currentPP = currentPP min maxPP
    }

  /** Does the move and returns the Array of MoveEvents that result from the actions. */
  def calculateMoveResults(thisPokemon: Pokemon, otherPokemon: Pokemon): Array[MoveEvent] =
    getMoveActions.flatMap(_.calculateResults(thisPokemon, otherPokemon))

  /** Lowers the current PP. */
  def decrementPP: Unit = currentPP -= 1

  /** Returns true if the current move has enough PP to be used. */
  def canUse: Boolean = currentPP > 0

  /** Returns the initial value for the move's max PP. */
  def getInitialMaxPP: Int

  /** Returns the maximum value for the move's max PP. */
  def getMaxMaxPP: Int

  /** Returns the move's power, or None if not applicable. */
  def getPower: Option[Int]

  /** Returns the move's accuracy. */
  def getAccuracy: Double

  /** Returns the move's description. */
  def getDescription: String

  /** Returns the move's type. */
  def getType: ElementalType

  /** Returns true if the move makes contact. */
  def makesContact: Boolean

  /** Returns the move's MoveActions in the order that they will be done. */
  def getMoveActions: Array[MoveAction]
}
