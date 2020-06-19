package model.pokemon.effect

import model.pokemon.Pokemon
import model.pokemon.move.MoveEvent
import model.statuseffect.{NonPersistentEffect, PersistentEffect, StatusEffect}

class EffectTracker {
  protected var effects: Set[StatusEffect] = Set.empty

  /** Returns the Set of effects. */
  def getEffects: Set[StatusEffect] = effects

  /** Returns the Set of persistent effects. */
  def getPersistentEffect: Option[PersistentEffect] = effects.filter(_.isPersistent).toList
    .asInstanceOf[List[PersistentEffect]] match {
    case List() => None
    case List(effect) => Some(effect)
    case _ => throw new UnsupportedOperationException("Too many persistent effects; max 1.")
  }

  /** Returns the Set of non-persistent effects. */
  def getNonPersistentEffects: Set[NonPersistentEffect] = effects.filterNot(_.isPersistent)
    .asInstanceOf[Set[NonPersistentEffect]]

  /** Adds the effect if not already present. */
  def addEffect(effect: StatusEffect): Unit =
    if(effect.isPersistent && getPersistentEffect.nonEmpty) throw new UnsupportedOperationException(
      "Cannot add second persistent effect.")
    else effects += effect

  /** Removes the effect if present. */
  def removeEffect(effect: StatusEffect): Unit = effects -= effect

  /** Removes all non-persistent effects. */
  def removeNonPersistentEffects(): Unit = getNonPersistentEffects.foreach(removeEffect)

  /** Removes the persistent effect, if there is one. */
  def removePersistentEffect(): Unit = getPersistentEffect.foreach(removeEffect)

  /** Removes all effects. */
  def clearEffects(): Unit = effects = Set.empty

  /** Returns true if the effect is in the Set. */
  def contains(effect: StatusEffect): Boolean = effects(effect)

  /** Returns an effect for which the predicate is true, if one exists. */
  def find(p: StatusEffect => Boolean): Option[StatusEffect] = effects.find(p)

  /** Returns the Array of Events that result from the given Effects. */
  protected def getEventsFromSomeEffects(someEffects: Array[StatusEffect], thisPokemon: Pokemon,
                                         otherPokemon: Pokemon): Array[MoveEvent] =
    someEffects
      .flatMap(_.getTurnlyActions(thisPokemon, otherPokemon))
      .flatMap(_.getResults(thisPokemon, otherPokemon))

  /** Returns the Array of Events that result from the before-move Effects this Pokemon has. */
  def getEventsFromBeforeMoveEffects(thisPokemon: Pokemon, otherPokemon: Pokemon): Array[MoveEvent] =
    getEventsFromSomeEffects(effects.toArray.filter(_.isBeforeMove).sorted, thisPokemon, otherPokemon)

  /** Returns the Array of Events that result from the after-move Effects this Pokemon has. */
  def getEventsFromAfterMoveEffects(thisPokemon: Pokemon, otherPokemon: Pokemon): Array[MoveEvent] =
    getEventsFromSomeEffects(effects.toArray.filterNot(_.isBeforeMove).sorted, thisPokemon, otherPokemon)
}
