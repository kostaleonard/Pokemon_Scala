package model.character

import model.party.Party

class PlayerCharacter(override protected val party: Party) extends Trainer(party) {

}
