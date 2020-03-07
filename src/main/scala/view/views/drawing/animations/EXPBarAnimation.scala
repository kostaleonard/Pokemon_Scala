package view.views.drawing.animations

import java.awt.{Color, Graphics2D}
import java.awt.image.BufferedImage

import model.battle.BattleInfoBox
import model.pokemon.Pokemon
import view.views.drawing.Animation

object EXPBarAnimation {
  val ANIMATION_FRAMES = 60
}

class EXPBarAnimation(callback: Option[() => Unit], battleInfoBox: BattleInfoBox, pokemon: Pokemon, amount: Int)
  extends Animation {
  setAnimationCallback(callback)
  protected var canvasImage: BufferedImage = new BufferedImage(getObjectWidth, getObjectHeight,
    BufferedImage.TYPE_INT_ARGB)
  protected var currentFrame = 0

  /** Returns the drawn pokemon. */
  def getPokemon: Pokemon = pokemon

  /** Returns the object's width. */
  override def getObjectWidth: Int = battleInfoBox.getObjectWidth

  /** Returns the object's height. */
  override def getObjectHeight: Int = battleInfoBox.getObjectHeight

  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  override def getImage: BufferedImage = {
    val g2d = canvasImage.getGraphics.asInstanceOf[Graphics2D]
    g2d.setFont(BattleInfoBox.HP_FONT)
    g2d.setColor(Color.BLACK)
    g2d.fillRect(0, getObjectHeight - 14, getObjectWidth, 14)
    g2d.setColor(BattleInfoBox.HP_FONT_COLOR)
    g2d.drawString("EXP", BattleInfoBox.BORDER_THICKNESS * 2, 87)
    val expFullWidth = getObjectWidth - 39
    g2d.setColor(BattleInfoBox.HP_BACKGROUND_COLOR)
    g2d.fillRect(37, getObjectHeight - 12, expFullWidth, 10)
    val drawEXP = (pokemon.getLevelTracker.getExperienceAtCurrentLevel + amount *
      (currentFrame.toDouble / EXPBarAnimation.ANIMATION_FRAMES)) min
      (pokemon.getLevelTracker.getTotalExperienceForNextLevel - pokemon.getLevelTracker.getTotalExperienceForCurrentLevel)
    val expActualWidth = expFullWidth min (expFullWidth * drawEXP /
      (pokemon.getLevelTracker.getTotalExperienceForNextLevel -
        pokemon.getLevelTracker.getTotalExperienceForCurrentLevel)).toInt
    g2d.setColor(BattleInfoBox.XP_FILL_COLOR)
    g2d.fillRect(37, getObjectHeight - 12, expActualWidth, 10)
    g2d.dispose()
    canvasImage
  }

  /** Returns the object's image, already scaled. This is to speed up rendering. */
  override def getPrescaledImage: Option[BufferedImage] = Some(getImage)

  /** Returns true if the animation is complete. */
  override def isAnimationComplete: Boolean = currentFrame >= EXPBarAnimation.ANIMATION_FRAMES

  /** Progresses animations by one frame. Parent objects should call on all child objects they render. */
  override def advanceFrame(): Unit = if(running) currentFrame += 1
}
