package model.statuseffect

import model.pokemon.Pokemon
import model.pokemon.move.{TurnlyTryThaw, MoveAction, TurnlyBurnDamage, TurnlyPoisonDamage}

sealed trait StatusEffect extends Ordered[StatusEffect] {
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

  /** Returns the List of MoveActions executed when the Pokemon receives this StatusEffect. */
  def getInitialActions(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveAction]

  /** Returns the List of MoveActions executed every turn while the Pokemon has this StatusEffect. */
  def getTurnlyActions(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveAction]
}

sealed trait PersistentEffect extends StatusEffect {
  /** Returns true if the status effect is persistent. */
  override def isPersistent: Boolean = true

  /** Returns the name of the status effect. */
  def getIdentifier: String

  /** Returns the List of MoveActions executed every 4 steps while the Pokemon has this StatusEffect. */
  def getOutOfBattleAction: List[MoveAction]
}

sealed trait NonPersistentEffect extends StatusEffect {
  /** Returns true if the status effect is persistent. */
  override def isPersistent: Boolean = false
}

case object Burn extends PersistentEffect {
  /** Returns the name of the status effect. */
  override def getIdentifier: String = "BRN"

  /** Returns the order in which this StatusEffect will be processed relative to other StatusEffects. By convention, a
    * negative number indicates that the StatusEffect is processed before the move, and a positive number indicates
    * after. This is just convention, and is actually controlled by isBeforeMove. */
  def getOrder: Int = BURN_ORDER

  /** Returns true if this StatusEffect is processed before the move happens; returns false if after. */
  def isBeforeMove: Boolean = false

  /** Returns an empty List. Burn does not have any initial effects. */
  override def getInitialActions(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveAction] = List.empty

  /** Returns the List of MoveActions executed every turn while the Pokemon has this StatusEffect. */
  override def getTurnlyActions(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveAction] = List(TurnlyBurnDamage)

  /** Returns an empty List. Burn does not have any outside of battle effects. */
  override def getOutOfBattleAction: List[MoveAction] = List.empty
}

case object Frozen extends PersistentEffect {
  val THAW_CHANCE = 0.2

  /** Returns the name of the status effect. */
  override def getIdentifier: String = "FRZ"

  /** Returns the order in which this StatusEffect will be processed relative to other StatusEffects. By convention, a
    * negative number indicates that the StatusEffect is processed before the move, and a positive number indicates
    * after. This is just convention, and is actually controlled by isBeforeMove. */
  def getOrder: Int = FROZEN_ORDER

  /** Returns true if this StatusEffect is processed before the move happens; returns false if after. */
  def isBeforeMove: Boolean = true

  /** Returns an empty List. Frozen does not have any initial effects. */
  override def getInitialActions(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveAction] = List.empty

  /** Returns the List of MoveActions executed every turn while the Pokemon has this StatusEffect. */
  override def getTurnlyActions(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveAction] = List(TurnlyTryThaw)

  /** Returns an empty List. Frozen does not have any outside of battle effects. */
  override def getOutOfBattleAction: List[MoveAction] = List.empty
}

case class Poison(badly: Boolean, turn: Int) extends PersistentEffect {
  /** Returns the name of the status effect. */
  override def getIdentifier: String = "PSN"

  /** Returns the order in which this StatusEffect will be processed relative to other StatusEffects. By convention, a
    * negative number indicates that the StatusEffect is processed before the move, and a positive number indicates
    * after. This is just convention, and is actually controlled by isBeforeMove. */
  def getOrder: Int = POISON_ORDER

  /** Returns true if this StatusEffect is processed before the move happens; returns false if after. */
  def isBeforeMove: Boolean = false

  /** Returns an empty List. Poison does not have any initial effects. */
  override def getInitialActions(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveAction] = List.empty

  /** Returns the List of MoveActions executed every turn while the Pokemon has this StatusEffect. */
  override def getTurnlyActions(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveAction] = {
    if(badly) List(TurnlyPoisonDamage(turn * 1.0 / 8.0)) //TODO magic numbers
    else List(TurnlyPoisonDamage(1.0 / 8.0)) //TODO magic numbers
  }

  //TODO poison should damage pokemon every 4 steps.
  /** TODO. */
  override def getOutOfBattleAction: List[MoveAction] = List.empty
}
