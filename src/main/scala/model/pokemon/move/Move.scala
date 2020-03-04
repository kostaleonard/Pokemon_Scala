package model.pokemon.move

import model.elementaltype.ElementalType
import model.pokemon.Pokemon

object Move {
  val BASE_CRITICAL_HIT_CHANCE = 0.0625

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
    getMoveActions.flatMap(_.getResults(thisPokemon, otherPokemon))

  /** Lowers the current PP. */
  def decrementPP: Unit = currentPP -= 1

  /** Returns true if the current move has enough PP to be used. */
  def canUse: Boolean = currentPP > 0

  /** Returns the chance of a critical hit. Subclasses may override. */
  def getCriticalHitChance: Double = Move.BASE_CRITICAL_HIT_CHANCE

  /** Returns the MoveEvents that are the result of thisPokemon using the move on otherPokemon.
    * I just want to say, it was pretty painful figuring out these type annotations. */
  def getEventsFromMove(thisPokemon: Pokemon, otherPokemon: Pokemon): Array[MoveEvent] =
    DisplayMessage("%s used %s!".format(thisPokemon.getName, getName)) +:
      getMoveActions.flatMap{ action =>
        var newHPSelf = thisPokemon.getCurrentStats.getHP
        var newHPOther = otherPokemon.getCurrentStats.getHP
        action.getResults(thisPokemon, otherPokemon).flatMap {
          case DealDamageToSelf(amount) =>
            newHPSelf = (thisPokemon.getCurrentStats.getHP - amount) max 0
            Array(PlayHPBarAnimation(thisPokemon, newHPSelf), DealDamageToSelf(amount)): Array[MoveEvent]
          case DealDamageToOpponent(amount) =>
            newHPOther = (otherPokemon.getCurrentStats.getHP - amount) max 0
            Array(PlayHPBarAnimation(otherPokemon, newHPOther), DealDamageToOpponent(amount)): Array[MoveEvent]
          case other => Array(other): Array[MoveEvent]
        }
      }

  /** Returns the name of the move, in all caps. */
  def getName: String

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

  /** Returns the path to the move's animation. */
  def getAnimationPath: String

  /** Returns the move's type. */
  def getType: ElementalType

  /** Returns true if the move makes contact. */
  def makesContact: Boolean

  /** Returns true if the move is physical. This influences whether the regular or special stats are used in the damage
    * calculation. */
  def isPhysical: Boolean

  /** Returns the move's MoveActions in the order that they will be done. */
  def getMoveActions: Array[MoveEventGenerator]
}
