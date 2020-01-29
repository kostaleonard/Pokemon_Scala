package controller

import model.pokemon.Pokemon
import view.View

sealed trait ControllerMessage

case class StartRandomEncounter(wildPokemon: Pokemon) extends ControllerMessage

case class SwitchViews(nextView: View) extends ControllerMessage

case class SendKeyPress(keyCode: Int) extends ControllerMessage
