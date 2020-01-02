package model

import model.board.BoardLibrary
import model.character.PlayerCharacter
import model.party.Party
import model.pokemon.exp.LevelTracker
import model.pokemon.species.MissingNo

import scala.collection.mutable.ListBuffer

object Model {
  final val MODEL_SERIAL_VERSION_UID = 2017L
}

/** Represents the save state of an entire game.
  * Anything that needs to be saved should be contained within the Model object so that it gets wrapped up during
  * serialization. */
@SerialVersionUID(Model.MODEL_SERIAL_VERSION_UID)
class Model(protected val profileName: String) extends Serializable {
  protected val boardLibrary = new BoardLibrary
  protected val playerCharacter = new PlayerCharacter(new Party(ListBuffer(new MissingNo(LevelTracker.create(1)))))
  //TODO the PC gets saved because it stores all the Pokemon not in the player's party.

  /** Returns the PlayerCharacter. */
  def getPlayerCharacter: PlayerCharacter = playerCharacter

  /** Returns the BoardLibrary. */
  def getBoardLibrary: BoardLibrary = boardLibrary
}
