package model.pokemon.effect

import model.pokemon.Pokemon
import model.pokemon.move.MoveEvent
import model.statuseffect.{NonPersistentEffect, PersistentEffect, StatusEffect}

class EffectTracker {
  protected var effects: Set[StatusEffect] = Set.empty

  /** Returns the Set of effects. */
  def getEffects: Set[StatusEffect] = effects

  /** Returns the Set of persistent effects. */
  def getPersistentEffects: Set[PersistentEffect] = effects.filter(_.isPersistent).asInstanceOf[Set[PersistentEffect]]

  /** Returns the Set of non-persistent effects. */
  def getNonPersistentEffects: Set[NonPersistentEffect] = effects.filterNot(_.isPersistent)
    .asInstanceOf[Set[NonPersistentEffect]]

  /** Adds the effect if not already present. */
  def addEffect(effect: StatusEffect): Unit = effects += effect

  /** Removes the effect if present. */
  def removeEffect(effect: StatusEffect): Unit = effects -= effect

  /** Returns true if the effect is in the Set. */
  def contains(effect: StatusEffect): Boolean = effects(effect)

  //TODO ordering of effects? Should only have one persistent effect, so only applies in limited cases.
  /** Returns the Array of Events that result from the Effects this Pokemon has. */
  def getEventsFromEffects(thisPokemon: Pokemon, otherPokemon: Pokemon): Array[MoveEvent] =
    effects.toArray.sortBy(_.toString)
      .flatMap(_.getTurnlyActions(thisPokemon, otherPokemon))
      .flatMap(_.getResults(thisPokemon, otherPokemon))
}
