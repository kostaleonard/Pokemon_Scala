package model.pokemon

import java.awt.{Color, Graphics2D, Image}
import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO
import model.board.Board
import model.board.cells.TallGrass
import model.elementaltype.ElementalType
import model.pokemon.effect.EffectTracker
import model.pokemon.exp.LevelTracker
import model.pokemon.move.{Move, MoveList}
import model.pokemon.stat.{BattleStats, IVStats, PokemonStats}
import view.{Drawable, View}

import scala.util.Random

object Pokemon {
  val NAME_MIN_LENGTH = 1
  val NAME_MAX_LENGTH = 15
  val DEFAULT_PERCENT_MALE = 0.5
  val UNICODE_MALE: String = "\u2642"
  val UNICODE_FEMALE: String = "\u2640"
  val COLOR_MALE: Color = Color.CYAN.darker()
  val COLOR_FEMALE: Color = Color.RED.brighter()
  val MAX_DRAW_WIDTH: Int = 64 * 4
  val MAX_DRAW_HEIGHT: Int = 64 * 4
  val FRAME_FRONT = 0
  val FRAME_BACK = 1

  /** Returns a stats object representing a new Pokemon's IVs. */
  def getRandomIVStats: IVStats = {
    def getRandomIV: Int = Random.nextInt(IVStats.MAX_VALUE) + IVStats.MIN_VALUE
    new IVStats(getRandomIV, getRandomIV, getRandomIV, getRandomIV, getRandomIV, getRandomIV)
  }

  /** Returns the standard stats of this Pokemon. This is Leo's modified version, without EV stats. I don't like EVs. */
  def getStandardStats(baseStats: PokemonStats, ivStats: IVStats, level: Int): PokemonStats = {
    //TODO is this working how I want it? Can I clean this up?
    new PokemonStats(
      PokemonStats.SORTED_KEYS.map(key =>
        if (key == PokemonStats.HP_KEY) key -> (((baseStats.getHP + ivStats.getHP) * 2 * level) / 100 + level + 10)
        else key -> (((baseStats.getStat(key) + ivStats.getStat(key)) * 2 * level) / 100 + 5)
      ).toMap
    )
  }

  /** The map of all frame numbers to their avatars. These are all of the images needed to render the pokemon. */
  def getSpeciesImageMap(speciesName: String): scala.collection.immutable.Map[Int, Image] =
    scala.collection.immutable.Map(
      FRAME_FRONT -> ImageIO.read(new File(View.getSourcePath("sprites/pokemon/%s_front.png".format(speciesName)))),
      FRAME_BACK -> ImageIO.read(new File(View.getSourcePath("sprites/pokemon/%s_back.png".format(speciesName))))
    )
}

/** Represents a Pokemon object. The reason why a Pokemon needs a LevelTracker object in the constructor--not just an
  * int representing its level--is because, during evolution, the experience gained needs to follow forward smoothly
  * when the Pokemon object is replaced with a new one of the evolved form. */
abstract class Pokemon(protected val levelTracker: LevelTracker) extends Drawable {
  protected val ivStats: IVStats = Pokemon.getRandomIVStats
  protected var standardStats: PokemonStats = Pokemon.getStandardStats(getBaseStats, ivStats, getLevel)
  protected var currentStats = new BattleStats(standardStats)
  protected val moveList: MoveList = getInitialMoveList(getLevel)
  protected var name: String = getSpeciesName
  protected val isMale: Boolean = Random.nextDouble() < getPercentMale
  protected val effectTracker: EffectTracker = new EffectTracker
  protected val speciesImageMap: scala.collection.immutable.Map[Int, Image] = Pokemon.getSpeciesImageMap(getSpeciesName)
  protected val prescaledImageFront: BufferedImage = getPrescaledImageFront.get
  protected val prescaledImageBack: BufferedImage = getPrescaledImageBack.get

  /** Returns the Pokemon's name. By default, this is the species name. */
  def getName: String = name

  /** Changes the name to the new value. */
  def setName(newName: String): Unit =
    if(newName.length < Pokemon.NAME_MIN_LENGTH) throw new UnsupportedOperationException("Name too short.")
    else if(newName.length > Pokemon.NAME_MAX_LENGTH) throw new UnsupportedOperationException("Name too long.")
    else name = newName

  /** Returns the Pokemon's current level. */
  def getLevel: Int = levelTracker.getLevel

  /** Returns the IV stats for this pokemon. */
  def getIVStats: IVStats = ivStats

  /** Returns the stats the Pokemon would have after healing. */
  def getStandardStats: PokemonStats = standardStats

  /** Returns the stats the Pokemon currently has. */
  def getCurrentStats: BattleStats = currentStats

  /** Returns true if the Pokemon is KO, false otherwise. */
  def isKO: Boolean = currentStats.isKO

  /** Decrements the current HP by the given amount. */
  def takeDamage(amount: Int): Unit = currentStats.takeDamage(amount)

  //TODO this should honestly not be accessible, and the methods from EffectTracker should be abstracted one level up. Don't want people to see the secret sauce beneath. But I don't really feel like rewriting those methods right now, in case things change significantly.
  /** Returns the EffectTracker. */
  def getEffectTracker: EffectTracker = effectTracker

  /** Returns the LevelTracker. */
  def getLevelTracker: LevelTracker = levelTracker

  /** Returns the MoveList. */
  def getMoveList: MoveList = moveList

  /** Returns the Pokemon's moves at a given level. */
  def getInitialMoveList(level: Int): MoveList = {
    val learnMap = getLearnMap
    val moveKeys = learnMap.keys.filter(_ <= level).toArray.sorted.reverse.take(MoveList.MAX_SIZE)
    val moveArray = moveKeys.map(learnMap(_))
    new MoveList(moveArray)
  }

  /** Returns the percentage of this species that are male. Subclasses may override. */
  def getPercentMale: Double = Pokemon.DEFAULT_PERCENT_MALE

  /** Returns true if the Pokemon is male (false if female). */
  def getIsMale: Boolean = isMale

  /** Returns the Pokemon's unicode gender sign. */
  def getGenderSign: String = if(isMale) Pokemon.UNICODE_MALE else Pokemon.UNICODE_FEMALE

  /** Returns the Pokemon's gender color. */
  def getGenderColor: Color = if(isMale) Pokemon.COLOR_MALE else Pokemon.COLOR_FEMALE

  /** Returns the Pokemon's pokedex number. */
  def getPokedexNum: Int

  /** Returns the Pokemon's pokedex entry. */
  def getPokedexEntry: String

  /** Returns the name of the Pokemon species. */
  def getSpeciesName: String

  /** Returns the base stats for the species. */
  def getBaseStats: PokemonStats

  /** Returns the Pokemon's types. */
  def getTypeArray: Array[ElementalType]

  /** Returns the Pokemon's learn map. */
  def getLearnMap: Map[Int, Move]

  /** Returns the object's width. */
  override def getObjectWidth: Int = Pokemon.MAX_DRAW_WIDTH

  /** Returns the object's height. */
  override def getObjectHeight: Int = Pokemon.MAX_DRAW_HEIGHT

  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  override def getImage: BufferedImage = throw new UnsupportedOperationException("Must call getImageFront or " +
    "getImageBack.")

  /** Returns the object's image, already scaled. This is to speed up rendering. */
  override def getPrescaledImage: Option[BufferedImage] = throw new UnsupportedOperationException("Must call " +
    "getPrescaledImageFront or getPrescaledImageBack.")

  /** Progresses animations by one frame. Parent objects should call on all child objects they render. */
  override def advanceFrame(): Unit = {}

  /** Returns the object's front image. */
  def getImageFront: BufferedImage = prescaledImageFront

  /** Returns the object's back image. */
  def getImageBack: BufferedImage = prescaledImageBack

  /** Returns the object's front image, already scaled. This is to speed up rendering. */
  def getPrescaledImageFront: Option[BufferedImage] = {
    val bufferedImage = new BufferedImage(getObjectWidth, getObjectHeight, BufferedImage.TYPE_INT_ARGB)
    val g2d = bufferedImage.getGraphics.asInstanceOf[Graphics2D]
    g2d.drawImage(
      speciesImageMap.getOrElse(Pokemon.FRAME_FRONT, ???), 0, 0, getObjectWidth, getObjectHeight, null)
    g2d.dispose()
    Some(bufferedImage)
  }

  /** Returns the object's back image, already scaled. This is to speed up rendering. */
  def getPrescaledImageBack: Option[BufferedImage] = {
    val bufferedImage = new BufferedImage(getObjectWidth, getObjectHeight, BufferedImage.TYPE_INT_ARGB)
    val g2d = bufferedImage.getGraphics.asInstanceOf[Graphics2D]
    g2d.drawImage(
      speciesImageMap.getOrElse(Pokemon.FRAME_BACK, ???), 0, 0, getObjectWidth, getObjectHeight, null)
    g2d.dispose()
    Some(bufferedImage)
  }
}
