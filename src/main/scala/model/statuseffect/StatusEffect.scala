package model.statuseffect

import model.pokemon.Pokemon
import model.pokemon.move.{MoveAction, TurnlyBurnDamage, TurnlyPoisonDamage}

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

case class Poison(badly: Boolean, turn: Int) extends PersistentEffect {
  /** Returns an empty List. Poison does not have any initial effects. */
  override def getInitialActions(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveAction] = List.empty

  /** Returns the List of MoveActions executed every turn while the Pokemon has this StatusEffect. */
  override def getTurnlyActions(thisPokemon: Pokemon, otherPokemon: Pokemon): List[MoveAction] = {
    if(badly) List(TurnlyPoisonDamage(turn * 1.0 / 8.0))
    else List(TurnlyPoisonDamage(1.0 / 8.0))
  }

  //TODO poison should damage pokemon every 4 steps.
  /** TODO. */
  override def getOutOfBattleAction: List[MoveAction] = List.empty
}
