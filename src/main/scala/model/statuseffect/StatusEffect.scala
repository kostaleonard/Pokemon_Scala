package model.statuseffect

import model.pokemon.Pokemon
import model.pokemon.move._
import view.views.drawing.Animation

sealed trait StatusEffect extends Ordered[StatusEffect] {
  val SLEEP_ORDER = -3
  val PARALYZE_ORDER = -2
  val FROZEN_ORDER = -1
  val BURN_ORDER = 1
  val POISON_ORDER = 2

  /** Defines an ordering on the StatusEffects for processing. */
  override def compare(that: StatusEffect): Int = getOrder.compare(that.getOrder)

  /** Returns the order in which this StatusEffect will be processed relative to other StatusEffects. By convention, a
    * negative number indicates that the StatusEffect is processed before the move, and a positive number indicates
    * after. This is just convention, and is actually controlled by isBeforeMove. */
  def getOrder: Int

  /** Returns true if this StatusEffect is processed before the move happens; returns false if after. */
  def isBeforeMove: Boolean

  /** Returns true if the status effect is persistent. */
  def isPersistent: Boolean

  /** Code to be executed when the Pokemon receives this StatusEffect. */
  def onEffectAdd(pokemon: Pokemon): Unit

  /** Code to be executed when the Pokemon removes this StatusEffect. */
  def onEffectRemove(pokemon: Pokemon): Unit

  //TODO this method does not seem to require these arguments.
  /** Returns the List of MoveActions executed every turn while the Pokemon has this StatusEffect. */
  def getTurnlyActions(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEventGenerator]

  /** Returns the move's animation from the player perspective. */
  def getPlayerAnimation: Option[Animation]

  /** Returns the move's animation from the opponent perspective. */
  def getOpponentAnimation: Option[Animation]
}

sealed trait PersistentEffect extends StatusEffect {
  /** Returns true if the status effect is persistent. */
  override def isPersistent: Boolean = true

  /** Returns true if getInitialActions should be performed every time the Pokemon first appears in battle. */
  def isInitialActionRecurrent: Boolean

  /** Returns the name of the status effect. */
  def getIdentifier: String

  /** Returns the List of MoveActions executed every 4 steps while the Pokemon has this StatusEffect. */
  def onFourSteps: List[MoveEventGenerator]
}

sealed trait NonPersistentEffect extends StatusEffect {
  /** Returns true if the status effect is persistent. */
  override def isPersistent: Boolean = false
}

case object Burn extends PersistentEffect {
  /** Returns the name of the status effect. */
  override def getIdentifier: String = "BRN"

  /** Returns true if getInitialActions should be performed every time the Pokemon first appears in battle. */
  override def isInitialActionRecurrent: Boolean = false

  /** Returns the order in which this StatusEffect will be processed relative to other StatusEffects. By convention, a
    * negative number indicates that the StatusEffect is processed before the move, and a positive number indicates
    * after. This is just convention, and is actually controlled by isBeforeMove. */
  def getOrder: Int = BURN_ORDER

  /** Returns true if this StatusEffect is processed before the move happens; returns false if after. */
  def isBeforeMove: Boolean = false

  /** Code to be executed when the Pokemon receives this StatusEffect. */
  override def onEffectAdd(pokemon: Pokemon): Unit = pokemon.getCurrentStats.setBurned(true)

  /** Code to be executed when the Pokemon removes this StatusEffect. */
  override def onEffectRemove(pokemon: Pokemon): Unit = pokemon.getCurrentStats.setBurned(false)

  /** Returns the List of MoveActions executed every turn while the Pokemon has this StatusEffect. */
  override def getTurnlyActions(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEventGenerator] =
    List(TurnlyBurnDamage)

  /** Returns an empty List. Burn does not have any outside of battle effects. */
  override def onFourSteps: List[MoveEventGenerator] = List.empty

  /** Returns the move's animation from the player perspective. */
  override def getPlayerAnimation: Option[Animation] = Some(Move.getPlaceholderOpponentAnimation)

  /** Returns the move's animation from the opponent perspective. */
  override def getOpponentAnimation: Option[Animation] = Some(Move.getPlaceholderPlayerAnimation)
}

case object Paralyze extends PersistentEffect {
  val NO_MOVE_CHANCE = 0.25

  /** Returns the name of the status effect. */
  override def getIdentifier: String = "PAR"

  /** Returns true if getInitialActions should be performed every time the Pokemon first appears in battle. */
  override def isInitialActionRecurrent: Boolean = true

  /** Returns the order in which this StatusEffect will be processed relative to other StatusEffects. By convention, a
    * negative number indicates that the StatusEffect is processed before the move, and a positive number indicates
    * after. This is just convention, and is actually controlled by isBeforeMove. */
  def getOrder: Int = PARALYZE_ORDER

  /** Returns true if this StatusEffect is processed before the move happens; returns false if after. */
  def isBeforeMove: Boolean = true

  /** Code to be executed when the Pokemon receives this StatusEffect. */
  override def onEffectAdd(pokemon: Pokemon): Unit = pokemon.getCurrentStats.setParalyzed(true)

  /** Code to be executed when the Pokemon removes this StatusEffect. */
  override def onEffectRemove(pokemon: Pokemon): Unit = pokemon.getCurrentStats.setParalyzed(false)

  /** Returns the List of MoveActions executed every turn while the Pokemon has this StatusEffect. */
  override def getTurnlyActions(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEventGenerator] =
    List(TurnlyParalysisCheck)

  /** Returns an empty List. Paralyze does not have any outside of battle effects. */
  override def onFourSteps: List[MoveEventGenerator] = List.empty

  /** Returns the move's animation from the player perspective. */
  override def getPlayerAnimation: Option[Animation] = Some(Move.getPlaceholderOpponentAnimation)

  /** Returns the move's animation from the opponent perspective. */
  override def getOpponentAnimation: Option[Animation] = Some(Move.getPlaceholderPlayerAnimation)
}

case class Sleep(turnsRemaining: Int) extends PersistentEffect {
  val MIN_TURNS = 1
  val MAX_TURNS = 5

  /** Returns the name of the status effect. */
  override def getIdentifier: String = "SLP"

  //TODO pretty sure about this boolean value.
  /** Returns true if getInitialActions should be performed every time the Pokemon first appears in battle. */
  override def isInitialActionRecurrent: Boolean = false

  /** Returns the order in which this StatusEffect will be processed relative to other StatusEffects. By convention, a
    * negative number indicates that the StatusEffect is processed before the move, and a positive number indicates
    * after. This is just convention, and is actually controlled by isBeforeMove. */
  def getOrder: Int = SLEEP_ORDER

  /** Returns true if this StatusEffect is processed before the move happens; returns false if after. */
  def isBeforeMove: Boolean = true

  /** Code to be executed when the Pokemon receives this StatusEffect. */
  override def onEffectAdd(pokemon: Pokemon): Unit = Unit

  /** Code to be executed when the Pokemon removes this StatusEffect. */
  override def onEffectRemove(pokemon: Pokemon): Unit = Unit

  /** Returns the List of MoveActions executed every turn while the Pokemon has this StatusEffect. */
  override def getTurnlyActions(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEventGenerator] =
    List(TurnlySleep(turnsRemaining))

  /** Returns an empty List. Sleep does not have any outside of battle effects. */
  override def onFourSteps: List[MoveEventGenerator] = List.empty

  /** Returns the move's animation from the player perspective. */
  override def getPlayerAnimation: Option[Animation] = Some(Move.getPlaceholderOpponentAnimation)

  /** Returns the move's animation from the opponent perspective. */
  override def getOpponentAnimation: Option[Animation] = Some(Move.getPlaceholderPlayerAnimation)
}

case object Frozen extends PersistentEffect {
  val THAW_CHANCE = 0.2

  /** Returns the name of the status effect. */
  override def getIdentifier: String = "FRZ"

  /** Returns true if getInitialActions should be performed every time the Pokemon first appears in battle. */
  override def isInitialActionRecurrent: Boolean = false

  /** Returns the order in which this StatusEffect will be processed relative to other StatusEffects. By convention, a
    * negative number indicates that the StatusEffect is processed before the move, and a positive number indicates
    * after. This is just convention, and is actually controlled by isBeforeMove. */
  def getOrder: Int = FROZEN_ORDER

  /** Returns true if this StatusEffect is processed before the move happens; returns false if after. */
  def isBeforeMove: Boolean = true

  /** Code to be executed when the Pokemon receives this StatusEffect. */
  override def onEffectAdd(pokemon: Pokemon): Unit = Unit

  /** Code to be executed when the Pokemon removes this StatusEffect. */
  override def onEffectRemove(pokemon: Pokemon): Unit = Unit

  /** Returns the List of MoveActions executed every turn while the Pokemon has this StatusEffect. */
  override def getTurnlyActions(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEventGenerator] =
    List(TurnlyTryThaw)

  /** Returns an empty List. Frozen does not have any outside of battle effects. */
  override def onFourSteps: List[MoveEventGenerator] = List.empty

  /** Returns the move's animation from the player perspective. */
  override def getPlayerAnimation: Option[Animation] = Some(Move.getPlaceholderOpponentAnimation)

  /** Returns the move's animation from the opponent perspective. */
  override def getOpponentAnimation: Option[Animation] = Some(Move.getPlaceholderPlayerAnimation)
}

//TODO could add another constructor arg to keep track of step % NUM_STEPS_TO_DAMAGE.
case class Poison(badly: Boolean, turn: Int) extends PersistentEffect {
  val NUM_STEPS_TO_DAMAGE = 4

  /** Returns the name of the status effect. */
  override def getIdentifier: String = "PSN"

  /** Returns the order in which this StatusEffect will be processed relative to other StatusEffects. By convention, a
    * negative number indicates that the StatusEffect is processed before the move, and a positive number indicates
    * after. This is just convention, and is actually controlled by isBeforeMove. */
  def getOrder: Int = POISON_ORDER

  /** Returns true if getInitialActions should be performed every time the Pokemon first appears in battle. */
  override def isInitialActionRecurrent: Boolean = false

  /** Returns true if this StatusEffect is processed before the move happens; returns false if after. */
  def isBeforeMove: Boolean = false

  /** Code to be executed when the Pokemon receives this StatusEffect. */
  override def onEffectAdd(pokemon: Pokemon): Unit = Unit

  /** Code to be executed when the Pokemon removes this StatusEffect. */
  override def onEffectRemove(pokemon: Pokemon): Unit = Unit

  //TODO the turn has to reset after every battle somehow.
  /** Returns the List of MoveActions executed every turn while the Pokemon has this StatusEffect. */
  override def getTurnlyActions(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveEventGenerator] = {
    if(badly) List(TurnlyPoisonDamage(turn * 1.0 / 8.0)) //TODO magic numbers
    else List(TurnlyPoisonDamage(1.0 / 8.0)) //TODO magic numbers
  }

  //TODO poison should damage pokemon every 4 steps.
  /** TODO. */
  override def onFourSteps: List[MoveEventGenerator] = List.empty

  /** Returns the move's animation from the player perspective. */
  override def getPlayerAnimation: Option[Animation] = Some(Move.getPlaceholderOpponentAnimation)

  /** Returns the move's animation from the opponent perspective. */
  override def getOpponentAnimation: Option[Animation] = Some(Move.getPlaceholderPlayerAnimation)
}
