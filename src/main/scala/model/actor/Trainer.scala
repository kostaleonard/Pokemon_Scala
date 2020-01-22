package model.actor

import model.party.Party

class Trainer(protected val party: Party) extends Actor {
  /** Returns the Trainer's party. */
  def getParty: Party = party
}
