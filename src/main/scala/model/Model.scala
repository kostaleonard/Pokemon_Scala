package model

import java.io._

import model.board.BoardLibrary
import model.character.PlayerCharacter
import model.party.Party
import model.pokemon.exp.LevelTracker
import model.pokemon.species.MissingNo

import scala.collection.mutable.ListBuffer

object Model {
  final val MODEL_SERIAL_VERSION_UID = 2017L
  val RESOURCE_ROOT_DIRECTORY = "resources"
  val PROFILE_DIRECTORY = "profiles"

  /** Returns the path to the given profile name. */
  def getProfilePath(profileName: String): String =
    RESOURCE_ROOT_DIRECTORY + "/" + PROFILE_DIRECTORY + "/" + profileName + ".profile"

  /** Returns the model if it exists under the given profile name; if not, creates a new model under that name. */
  def loadOrCreate(profileName: String): Model = {
    if(profileExists(profileName)) load(profileName) else create(profileName)
  }

  /** Returns true if the given profile exists. */
  def profileExists(profileName: String): Boolean = {
    val sourcePath = getProfilePath(profileName)
    new File(sourcePath).exists
  }

  /** Returns the model under a specific profile name. */
  def load(profileName: String): Model = {
    val sourcePath = getProfilePath(profileName)
    val ois = new ObjectInputStream(new FileInputStream(sourcePath))
    val model = ois.readObject.asInstanceOf[Model]
    ois.close()
    model
  }

  /** Creates and returns a model under the given profile name. */
  def create(profileName: String): Model = {
    val model = new Model(profileName)
    model
  }
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

  /** Writes the model to the output file for the profile name. */
  def save(): Unit = {
    val destinationPath = Model.getProfilePath(profileName)
    val oos = new ObjectOutputStream(new FileOutputStream(destinationPath))
    oos.writeObject(this)
    oos.close()
  }
}
