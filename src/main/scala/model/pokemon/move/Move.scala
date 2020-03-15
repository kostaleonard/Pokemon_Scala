package model.pokemon.move

import model.elementaltype.ElementalType
import model.pokemon.Pokemon
import view.views.drawing.Animation
import view.views.drawing.animations.EmberAnimation

object Move {
  val BASE_CRITICAL_HIT_CHANCE = 0.0625

  /** Returns standard max max PP for a given max PP. These are more like PP guidelines than laws. */
  def getMaxMaxPP(maxPP: Int): Int = maxPP * 8 / 5

  /** Returns a placeholder animation for moves whose animations have not yet been implemented. */
  def getPlaceholderAnimation: Animation = new EmberAnimation(None)

  /** Returns the turnly poison animation. */
  def getTurnlyPoisonAnimation: Animation = getPlaceholderAnimation

  /** Returns the turnly poison animation. */
  def getTurnlyBurnAnimation: Animation = getPlaceholderAnimation
}

abstract class Move {
  protected var maxPP: Int = getInitialMaxPP
  protected var currentPP: Int = getMaxPP

  /** Returns the current move PP. */
  def getCurrentPP: Int = currentPP

  /** Returns the current move's Max PP. This can change, up to the MaxMaxPP. */
  def getMaxPP: Int = maxPP

  /** Sets the current PP to the max PP. */
  def replenishPP(): Unit = currentPP = maxPP

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
  def decrementPP(): Unit = currentPP -= 1

  /** Returns true if the current move has enough PP to be used. */
  def canUse: Boolean = currentPP > 0

  /** Returns the chance of a critical hit. Subclasses may override. */
  def getCriticalHitChance: Double = Move.BASE_CRITICAL_HIT_CHANCE

  /** Returns the MoveEvents that are the result of thisPokemon using the move on otherPokemon.
    * I just want to say, it was pretty painful figuring out these type annotations. */
  def getEventsFromMove(thisPokemon: Pokemon, otherPokemon: Pokemon): Array[MoveEvent] =
    DisplayMessage("%s used %s!".format(thisPokemon.getName, getName)) +:
      getMoveActions.flatMap(_.getResults(thisPokemon, otherPokemon))

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

  /** Returns the move's animation. */
  def getAnimation: Animation

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
