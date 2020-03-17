package model.pokemon.move

import model.pokemon.Pokemon
import model.statuseffect._
import view.views.drawing.Animation

object MoveEvent {
  val DISPLAY_CRITICAL_HIT = DisplayMessage("Critical hit!")
  val DISPLAY_NOT_VERY_EFFECTIVE = DisplayMessage("It's not very effective...")
  val DISPLAY_SUPER_EFFECTIVE = DisplayMessage("It's super effective!")
  val DISPLAY_BUT_IT_FAILED = DisplayMessage("But it failed...")

  /** Returns the DisplayMessage used when a move misses. */
  def getDisplayMessageMoveMissed(thisPokemonName: String): DisplayMessage =
    DisplayMessage("%s missed.".format(thisPokemonName))

  /** Returns the DisplayMessage used when a move misses. */
  def getDisplayMessageMoveNoEffect(moveName: String, otherPokemonName: String): DisplayMessage =
    DisplayMessage("%s does not affect %s.".format(moveName, otherPokemonName))

  /** Returns the DisplayMessage used when a Pokemon is burned. */
  def getDisplayMessageBurned(pokemonName: String): DisplayMessage =
    DisplayMessage("%s was burned.".format(pokemonName))

  /** Returns the DisplayMessage used when a Pokemon is hurt by its burn. */
  def getDisplayMessageHurtByBurn(pokemonName: String): DisplayMessage =
    DisplayMessage("%s was hurt by its burn.".format(pokemonName))

  /** Returns the DisplayMessage used when a Pokemon is poisoned. */
  def getDisplayMessagePoisoned(pokemonName: String, badly: Boolean): DisplayMessage =
    if(badly) DisplayMessage("%s was badly poisoned.".format(pokemonName))
    else DisplayMessage("%s was poisoned.".format(pokemonName))

  /** Returns the DisplayMessage used when a Pokemon is hurt by poison. */
  def getDisplayMessageHurtByPoison(pokemonName: String): DisplayMessage =
    DisplayMessage("%s was hurt by poison.".format(pokemonName))

  /** Returns the DisplayMessage used when a Pokemon is poisoned. */
  def getDisplayMessageFellAsleep(pokemonName: String): DisplayMessage =
    DisplayMessage("%s fell asleep!".format(pokemonName))

  /** Returns the DisplayMessage used when a Pokemon has thawed. */
  def getDisplayMessageThawed(pokemonName: String): DisplayMessage =
    DisplayMessage("%s has thawed out!".format(pokemonName))

  /** Returns the DisplayMessage used when a Pokemon is frozen solid. */
  def getDisplayMessageFrozenSolid(pokemonName: String): DisplayMessage =
    DisplayMessage("%s is frozen solid.".format(pokemonName))

  /** Returns the DisplayMessage used when a Pokemon's stat won't go lower. */
  def getDisplayMessageStatWillNotGoLower(pokemonName: String, statName: String): DisplayMessage =
    DisplayMessage("%s's %s won't go any lower!".format(pokemonName, statName))

  /** Returns the DisplayMessage used when a Pokemon's stat won't go higher. */
  def getDisplayMessageStatWillNotGoHigher(pokemonName: String, statName: String): DisplayMessage =
    DisplayMessage("%s's %s won't go any higher!".format(pokemonName, statName))

  /** Returns the DisplayMessage used when a Pokemon's stat falls. */
  def getDisplayMessageStatFell(pokemonName: String, statName: String): DisplayMessage =
    DisplayMessage("%s's %s fell.".format(pokemonName, statName))

  /** Returns the DisplayMessage used when a Pokemon's stat rises. */
  def getDisplayMessageStatRose(pokemonName: String, statName: String): DisplayMessage =
    DisplayMessage("%s's %s rose.".format(pokemonName, statName))

  /** Returns the DisplayMessage used when a Pokemon faints. */
  def getDisplayMessageFainted(pokemonName: String): DisplayMessage =
    DisplayMessage("%s fainted!".format(pokemonName))
}

sealed trait MoveEvent {
  /** Executes the results of the various MoveAction objects. */
  def doEvent(thisPokemon: Pokemon, otherPokemon: Pokemon): Unit
}

case class DisplayMessage(message: String) extends MoveEvent {
  /** DisplayMessage changes nothing in the model, but is used to send a message to the view. This message will be sent
    * to the user. */
  override def doEvent(thisPokemon: Pokemon, otherPokemon: Pokemon): Unit = {}
}

case class PlayMoveAnimation(move: Move) extends MoveEvent {
  /** PlayAnimation changes nothing in the model, but is used to send a message to the view. This message will be sent
    * to the user. */
  override def doEvent(thisPokemon: Pokemon, otherPokemon: Pokemon): Unit = {}
}

case class PlayEffectAnimation(effect: StatusEffect) extends MoveEvent {
  /** PlayAnimation changes nothing in the model, but is used to send a message to the view. This message will be sent
    * to the user. */
  override def doEvent(thisPokemon: Pokemon, otherPokemon: Pokemon): Unit = {}
}

case class PlayAnimationFromSource(path: String) extends MoveEvent {
  /** PlayAnimation changes nothing in the model, but is used to send a message to the view. This message will be sent
    * to the user. */
  override def doEvent(thisPokemon: Pokemon, otherPokemon: Pokemon): Unit = {}
}

case class PlayHPBarAnimation(pokemon: Pokemon, newHP: Int) extends MoveEvent {
  /** PlayAnimation changes nothing in the model, but is used to send a message to the view. This message will be sent
    * to the user. */
  override def doEvent(thisPokemon: Pokemon, otherPokemon: Pokemon): Unit = {}
}

case class DealDamageToOpponent(amount: Int) extends MoveEvent {
  /** Deals damage to the opponent. */
  override def doEvent(thisPokemon: Pokemon, otherPokemon: Pokemon): Unit = otherPokemon.takeDamage(amount)
}

case class DealDamageToSelf(amount: Int) extends MoveEvent {
  /** Deals damage to this Pokemon. */
  override def doEvent(thisPokemon: Pokemon, otherPokemon: Pokemon): Unit = thisPokemon.takeDamage(amount)
}

case object FaintOther extends MoveEvent {
  /** Signals that the other Pokemon has fainted. */
  override def doEvent(thisPokemon: Pokemon, otherPokemon: Pokemon): Unit = {}
}

case object FaintSelf extends MoveEvent {
  /** Signals that this Pokemon has fainted. */
  override def doEvent(thisPokemon: Pokemon, otherPokemon: Pokemon): Unit = {}
}

case class LowerStatOther(statKey: String, stages: Int) extends MoveEvent {
  /** Lowers the opponent's given stat by the given number of stages.  */
  override def doEvent(thisPokemon: Pokemon, otherPokemon: Pokemon): Unit =
    otherPokemon.getCurrentStats.incrementStage(statKey, -1 * stages)
}

case class InflictEffectOnOpponent(effect: StatusEffect) extends MoveEvent {
  /** Inflicts the given effect to the opponent. */
  override def doEvent(thisPokemon: Pokemon, otherPokemon: Pokemon): Unit =
    otherPokemon.getEffectTracker.addEffect(effect)
}

case object EndMove extends MoveEvent {
  /** Does nothing. */
  override def doEvent(thisPokemon: Pokemon, otherPokemon: Pokemon): Unit = Unit
}

case object WorsenPoisonSelf extends MoveEvent {
  /** Replaces the Pokemon's Poison with Poison that has progressed one turn. */
  override def doEvent(thisPokemon: Pokemon, otherPokemon: Pokemon): Unit = {
    //TODO this seems ugly.
    val poison = thisPokemon.getEffectTracker.getEffects.find {
      case Poison(badly, turn) => true
      case _ => false
    }.get.asInstanceOf[Poison]
    thisPokemon.getEffectTracker.removeEffect(poison)
    thisPokemon.getEffectTracker.addEffect(poison.copy(turn = poison.turn + 1))
  }
}

case object DecrementSleepCounterSelf extends MoveEvent {
  /** Replaces the Pokemon's Sleep with Sleep that has progressed one turn. */
  override def doEvent(thisPokemon: Pokemon, otherPokemon: Pokemon): Unit = {
    val sleep = thisPokemon.getEffectTracker.getEffects.find(_ match{
      case Sleep(turnsRemaining) => true
      case _ => false
    }).get.asInstanceOf[Sleep]
    thisPokemon.getEffectTracker.removeEffect(sleep)
    //TODO only add back if turnsRemaining > 1, but either way you need to display a message saying that the Pokemon is fast asleep or that it woke up.
    thisPokemon.getEffectTracker.addEffect(sleep.copy(turnsRemaining = sleep.turnsRemaining - 1))
  } 
}

case object RemoveFrozenSelf extends MoveEvent {
  /** Removes the Frozen status effect from this Pokemon */
  override def doEvent(thisPokemon: Pokemon, otherPokemon: Pokemon): Unit = thisPokemon.getEffectTracker.removeEffect(
    thisPokemon.getEffectTracker.getPersistentEffect.find(_.getIdentifier == Frozen.getIdentifier).get)
}
