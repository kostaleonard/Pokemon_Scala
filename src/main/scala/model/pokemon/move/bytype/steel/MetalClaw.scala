package model.pokemon.move.bytype.steel

import model.elementaltype.{ElementalType, SteelType}
import model.pokemon.move._
import model.pokemon.stat.PokemonStats
import view.views.drawing.Animation

object MetalClaw {
  val LOWER_ATK_CHANCE = 0.5
}

class MetalClaw extends Move {
  /** Returns the name of the move, in all caps. */
  override def getName: String = "METAL CLAW"

  /** Returns the initial value for the move's max PP. */
  override def getInitialMaxPP: Int = 35

  /** Returns the maximum value for the move's max PP. */
  override def getMaxMaxPP: Int = Move.getMaxMaxPP(getInitialMaxPP)

  /** Returns the move's power, or None if not applicable. */
  override def getPower: Option[Int] = Some(50)

  /** Returns the move's accuracy. */
  override def getAccuracy: Double = 0.95

  /** Returns the move's description. */
  override def getDescription: String = "The foe is attacked with steel claws. It may also raise the user's ATK."

  /** Returns the move's animation from the player perspective. */
  override def getPlayerAnimation: Animation = Move.getPlaceholderPlayerAnimation

  /** Returns the move's animation from the opponent perspective. */
  override def getOpponentAnimation: Animation = Move.getPlaceholderOpponentAnimation

  /** Returns the move's type. */
  override def getType: ElementalType = SteelType

  /** Returns true if the move makes contact. */
  override def makesContact: Boolean = true

  /** Returns true if the move is physical. This influences whether the regular or special stats are used in the damage
    * calculation. */
  override def isPhysical: Boolean = true

  /** Returns the move's MoveActions in the order that they will be done. */
  override def getMoveActions: Array[MoveEventGenerator] = Array(AccuracyCheck(getAccuracy), MoveDamage(this),
    TryLowerStatOther(PokemonStats.ATK_KEY, 1, MetalClaw.LOWER_ATK_CHANCE, false, None))
}
