package model.statuseffect

import model.pokemon.Pokemon
import model.pokemon.move.{MoveAction, TurnlyBurnDamage}

sealed trait StatusEffect {
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

  /** Returns the List of MoveActions executed every 4 steps while the Pokemon has this StatusEffect. */
  def getOutOfBattleAction: List[MoveAction]
}

sealed trait NonPersistentEffect extends StatusEffect {
  /** Returns true if the status effect is persistent. */
  override def isPersistent: Boolean = false
}

case object Burn extends PersistentEffect {
  /** Returns an empty List. Burn does not have any initial effects. */
  override def getInitialActions(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveAction] = List.empty

  /** Returns the List of MoveActions executed every turn while the Pokemon has this StatusEffect. */
  override def getTurnlyActions(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveAction] = List(TurnlyBurnDamage)

  /** Returns an empty List. Burn does not have any outside of battle effects. */
  override def getOutOfBattleAction: List[MoveAction] = List.empty
}
