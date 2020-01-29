package model.board.cells

import java.awt.{Color, Graphics2D, Image}
import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO
import model.board.Board
import model.pokemon.Pokemon
import model.pokemon.exp.LevelTracker
import model.pokemon.species.{Bulbasaur, MissingNo}
import view.View

object TallGrass {
  val TALL_GRASS_IMAGE: Image = ImageIO.read(new File(View.getSourcePath("tiles/tall_grass_1.png")))
  val DEFAULT_RANDOM_ENCOUNTER_CHANCE = 0.1
}

class TallGrass extends Cell {
  val prescaledImage: BufferedImage = getPrescaledImage.get
  randomEncounterChance = TallGrass.DEFAULT_RANDOM_ENCOUNTER_CHANCE
  wildPokemonMap = scala.collection.immutable.Map(
    (() => new Bulbasaur(LevelTracker.create(1))) -> 1
  )

  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  override def getImage: BufferedImage = prescaledImage

  /** Returns the object's image, already scaled. This is to speed up rendering. */
  override def getPrescaledImage: Option[BufferedImage] = {
    val bufferedImage = new BufferedImage(Board.TILE_SIZE, Board.TILE_SIZE, BufferedImage.TYPE_INT_ARGB)
    val g2d = bufferedImage.getGraphics.asInstanceOf[Graphics2D]
    g2d.drawImage(TallGrass.TALL_GRASS_IMAGE, 0, 0, Board.TILE_SIZE, Board.TILE_SIZE, null)
    g2d.dispose()
    Some(bufferedImage)
  }

  /** Progresses animations by one frame. Parent objects should call on all child objects they render. */
  override def advanceFrame(): Unit = {}
}
