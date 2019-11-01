package model.pokemon.effect

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
}
