package model.pokemon.move

import model.pokemon.Pokemon

sealed trait MoveEvent {
  /** Executes the results of the various MoveAction objects. */
  def doEvent(thisPokemon: Pokemon, otherPokemon: Pokemon): Unit
}

case class DisplayMessage(message: String) extends MoveEvent {
  /** DisplayMessage changes nothing in the model, but is used to send a message to the view. This message will be sent
    * to the user. */
  override def doEvent(thisPokemon: Pokemon, otherPokemon: Pokemon): Unit = {}
}

case object DisplayCriticalHit extends DisplayMessage("Critical hit!")

case object DisplayNotVeryEffective extends DisplayMessage("It's not very effective...")

case object DisplaySuperEffective extends DisplayMessage("It's super effective!")

case class DisplayMoveDoesNotAffect(moveName: String, otherPokemonName: String)
  extends DisplayMessage("%s does not affect foe %s".format(moveName, otherPokemonName))

//TODO animations--how to show which animation to play?
case class PlayAnimation(path: String) extends MoveEvent {
  /** PlayAnimation changes nothing in the model, but is used to send a message to the view. This message will be sent
    * to the user. */
  override def doEvent(thisPokemon: Pokemon, otherPokemon: Pokemon): Unit = {}
}

case class DealDamageToOpponent(amount: Int) extends MoveEvent {
  /** Deals damage to the opponent. */
  override def doEvent(thisPokemon: Pokemon, otherPokemon: Pokemon): Unit = otherPokemon.takeDamage(amount)
}
