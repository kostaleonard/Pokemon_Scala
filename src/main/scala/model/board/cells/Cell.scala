package model.board.cells

import java.awt.image.BufferedImage

import model.board.{Board, BoardObject}
import model.pokemon.Pokemon
import model.pokemon.exp.LevelTracker
import model.pokemon.species.MissingNo
import view.Drawable

import scala.util.Random

abstract class Cell extends Drawable {
  protected var boardObject: Option[BoardObject] = None
  protected var randomEncounterChance: Double = 0.0
  protected var wildPokemonMap: scala.collection.immutable.Map[() => Pokemon, Int] = scala.collection.immutable.Map(
    (() => new MissingNo(LevelTracker.create(1))) -> 1
  )

  /** Returns the chance of a random encounter on this cell. */
  def getRandomEncounterChance: Double = randomEncounterChance

  /** Returns a Map of functions that generate Pokemon, and the relative frequency with which that function should be
    * chosen. */
  def getWildPokemonMap: scala.collection.immutable.Map[() => Pokemon, Int] = wildPokemonMap

  /** Sets a Map of functions that generate Pokemon, and the relative frequency with which that function should be
    * chosen. */
  def setWildPokemonMap(m: scala.collection.immutable.Map[() => Pokemon, Int]): Unit = wildPokemonMap = m

  /** Returns a new wild pokemon */
  def getRandomWildPokemon: Pokemon = {
    val pokemonGeneratorFunctions: Array[() => Pokemon] =
      wildPokemonMap.keys.flatMap(f => Array.fill(wildPokemonMap(f))(f)).toArray
    val index = Random.nextInt(pokemonGeneratorFunctions.length)
    pokemonGeneratorFunctions(index).apply()
  }

  /** Returns the Cell's BoardObject. */
  def getBoardObject: Option[BoardObject] = boardObject

  /** Sets the stored BoardObject to the given value. */
  def setBoardObject(obj: Option[BoardObject]): Unit = boardObject = obj

  /** Returns the object's width. */
  def getObjectWidth: Int = Board.TILE_SIZE

  /** Returns the object's height. */
  def getObjectHeight: Int = Board.TILE_SIZE

  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  def getImage: BufferedImage
}
