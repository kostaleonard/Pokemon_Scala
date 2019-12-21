package model

import model.board.BoardLibrary
import model.character.PlayerCharacter
import model.party.Party
import model.pokemon.exp.LevelTracker
import model.pokemon.species.MissingNo

import scala.collection.mutable.ListBuffer

class Model(protected val profileName: String) {
  protected val boardLibrary = new BoardLibrary
  protected val player = new PlayerCharacter(new Party(ListBuffer(new MissingNo(LevelTracker.create(1)))))

}
