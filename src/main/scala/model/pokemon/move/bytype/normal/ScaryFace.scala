package model.pokemon.move.bytype.normal

import model.elementaltype.{ElementalType, NormalType}
import model.pokemon.move._
import model.pokemon.stat.PokemonStats
import view.views.drawing.Animation

class ScaryFace extends Move {
  /** Returns the name of the move, in all caps. */
  override def getName: String = "SCARY FACE"

  /** Returns the initial value for the move's max PP. */
  override def getInitialMaxPP: Int = 10

  /** Returns the maximum value for the move's max PP. */
  override def getMaxMaxPP: Int = Move.getMaxMaxPP(getInitialMaxPP)

  /** Returns the move's power, or None if not applicable. */
  override def getPower: Option[Int] = None

  /** Returns the move's accuracy. */
  override def getAccuracy: Double = 1.0

  /** Returns the move's description. */
  override def getDescription: String = "Frightens the foe with a scary face to sharply reduce its SPEED."

  /** Returns the move's animation from the player perspective. */
  override def getPlayerAnimation: Animation = Move.getPlaceholderPlayerAnimation

  /** Returns the move's animation from the opponent perspective. */
  override def getOpponentAnimation: Animation = Move.getPlaceholderOpponentAnimation

  /** Returns the move's type. */
  override def getType: ElementalType = NormalType

  /** Returns true if the move makes contact. */
  override def makesContact: Boolean = false

  /** Returns true if the move is physical. This influences whether the regular or special stats are used in the damage
    * calculation. */
  override def isPhysical: Boolean = false

  /** Returns the move's MoveActions in the order that they will be done. */
  override def getMoveActions: Array[MoveEventGenerator] =
    Array(AccuracyCheck(getAccuracy), TryLowerStatOther(PokemonStats.SPD_KEY, 2, 1.0, true, Some(this)))
}