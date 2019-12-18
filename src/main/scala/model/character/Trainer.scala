package model.character

import model.party.Party

class Trainer(protected val party: Party) extends Character {
  /** Returns the Trainer's party. */
  def getParty: Party = party
}
