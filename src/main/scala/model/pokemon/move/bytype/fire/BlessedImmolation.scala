package model.pokemon.move.bytype.fire

import model.elementaltype.{ElementalType, FireType}
import model.pokemon.move._
import view.views.drawing.Animation

object BlessedImmolation {
  val BURN_CHANCE = 0.5
}

class BlessedImmolation extends Move {
  /** Returns true if the "[Pokemon] used [move]!" message should display. Subclasses may override. */
  override def displayUsedMessage: Boolean = false

  /** Returns the name of the move, in all caps. */
  override def getName: String = "BLESSED IMMOLATION"

  /** Returns the initial value for the move's max PP. */
  override def getInitialMaxPP: Int = 5

  /** Returns the maximum value for the move's max PP. */
  override def getMaxMaxPP: Int = Move.getMaxMaxPP(getInitialMaxPP)

  /** Returns the move's power, or None if not applicable. */
  override def getPower: Option[Int] = Some(350)

  /** Returns the move's accuracy. */
  override def getAccuracy: Double = 1.0

  /** Returns the move's description. */
  override def getDescription: String = "Absorbs light for two turns, then explodes with the heat of the sun."

  /** Returns the move's animation from the player perspective. */
  override def getPlayerAnimation: Animation = Move.getPlaceholderPlayerAnimation

  /** Returns the move's animation from the opponent perspective. */
  override def getOpponentAnimation: Animation = Move.getPlaceholderOpponentAnimation

  /** Returns the move's type. */
  override def getType: ElementalType = FireType

  /** Returns true if the move makes contact. */
  override def makesContact: Boolean = false

  /** Returns true if the move is physical. This influences whether the regular or special stats are used in the damage
    * calculation. */
  override def isPhysical: Boolean = false

  /** Returns the move's MoveActions in the order that they will be done. */
  override def getMoveActions: Array[MoveEventGenerator] = Array(UseMultiMove(
    List(new BlessedImmolationCharging,
      new BlessedImmolationCharging,
      new BlessedImmolationAttacking(getPower, getAccuracy))))

  class BlessedImmolationCharging extends Move {
    /** Returns true if the "[Pokemon] used [move]!" message should display. Subclasses may override. */
    override def displayUsedMessage: Boolean = false

    /** Returns the name of the move, in all caps. */
    override def getName: String = ""

    /** Returns the initial value for the move's max PP. */
    override def getInitialMaxPP: Int = 1

    /** Returns the maximum value for the move's max PP. */
    override def getMaxMaxPP: Int = 1

    /** Returns the move's power, or None if not applicable. */
    override def getPower: Option[Int] = None

    /** Returns the move's accuracy. */
    override def getAccuracy: Double = 1.0

    /** Returns the move's description. */
    override def getDescription: String = ""

    /** Returns the move's animation from the player perspective. */
    override def getPlayerAnimation: Animation = Move.getPlaceholderPlayerAnimation

    /** Returns the move's animation from the opponent perspective. */
    override def getOpponentAnimation: Animation = Move.getPlaceholderOpponentAnimation

    /** Returns the move's type. */
    override def getType: ElementalType = FireType

    /** Returns true if the move makes contact. */
    override def makesContact: Boolean = false

    /** Returns true if the move is physical. This influences whether the regular or special stats are used in the damage
      * calculation. */
    override def isPhysical: Boolean = false

    /** Returns the move's MoveActions in the order that they will be done. */
    override def getMoveActions: Array[MoveEventGenerator] = Array(AnimationGenerator(this), TakeInSunlight)
  }

  class BlessedImmolationAttacking(power: Option[Int], accuracy: Double) extends Move {
    /** Returns the name of the move, in all caps. */
    override def getName: String = "BLESSED IMMOLATION"

    /** Returns the initial value for the move's max PP. */
    override def getInitialMaxPP: Int = 1

    /** Returns the maximum value for the move's max PP. */
    override def getMaxMaxPP: Int = 1

    /** Returns the move's power, or None if not applicable. */
    override def getPower: Option[Int] = power

    /** Returns the move's accuracy. */
    override def getAccuracy: Double = accuracy

    /** Returns the move's description. */
    override def getDescription: String = ""

    /** Returns the move's animation from the player perspective. */
    override def getPlayerAnimation: Animation = Move.getPlaceholderPlayerAnimation

    /** Returns the move's animation from the opponent perspective. */
    override def getOpponentAnimation: Animation = Move.getPlaceholderOpponentAnimation

    /** Returns the move's type. */
    override def getType: ElementalType = FireType

    /** Returns true if the move makes contact. */
    override def makesContact: Boolean = false

    /** Returns true if the move is physical. This influences whether the regular or special stats are used in the damage
      * calculation. */
    override def isPhysical: Boolean = false

    /** Returns the move's MoveActions in the order that they will be done. */
    override def getMoveActions: Array[MoveEventGenerator] = Array(
      AccuracyCheck(getAccuracy), MoveDamage(this), TryBurn(BlessedImmolation.BURN_CHANCE, false, None),
      ThawFrozenOther)
  }
}
