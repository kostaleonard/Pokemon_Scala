package model.pokemon.move.bytype.water

import model.elementaltype.{ElementalType, WaterType}
import model.pokemon.move._
import model.pokemon.stat.PokemonStats
import view.views.drawing.Animation

object Bubble {
  val SLOW_CHANCE = 0.1
}

class Bubble extends Move {
  /** Returns the name of the move, in all caps. */
  override def getName: String = "BUBBLE"

  /** Returns the initial value for the move's max PP. */
  override def getInitialMaxPP: Int = 30

  /** Returns the maximum value for the move's max PP. */
  override def getMaxMaxPP: Int = Move.getMaxMaxPP(getInitialMaxPP)

  /** Returns the move's power, or None if not applicable. */
  override def getPower: Option[Int] = Some(40)

  /** Returns the move's accuracy. */
  override def getAccuracy: Double = 1.0

  /** Returns the move's description. */
  override def getDescription: String = "A spray of bubbles hits the foe. It may lower the foe's SPEED."

  /** Returns the move's animation from the player perspective. */
  override def getPlayerAnimation: Animation = Move.getPlaceholderPlayerAnimation

  /** Returns the move's animation from the opponent perspective. */
  override def getOpponentAnimation: Animation = Move.getPlaceholderOpponentAnimation

  /** Returns the move's type. */
  override def getType: ElementalType = WaterType

  /** Returns true if the move makes contact. */
  override def makesContact: Boolean = false

  /** Returns true if the move is physical. This influences whether the regular or special stats are used in the damage
    * calculation. */
  override def isPhysical: Boolean = false

  /** Returns the move's MoveActions in the order that they will be done. */
  override def getMoveActions: Array[MoveEventGenerator] = Array(AccuracyCheck(getAccuracy), MoveDamage(this),
      TryLowerStatOther(PokemonStats.SPD_KEY, 1, Bubble.SLOW_CHANCE, false, None))
}
