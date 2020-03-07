package view.views.drawing.animations

import java.awt.{Color, Graphics2D}
import java.awt.image.BufferedImage

import model.battle.BattleInfoBox
import model.pokemon.Pokemon
import view.views.drawing.Animation

object HPBarAnimation {
  val ANIMATION_FRAMES = 60
}

class HPBarAnimation(callback: Option[() => Unit], battleInfoBox: BattleInfoBox, pokemon: Pokemon, amount: Int)
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
    g2d.setColor(BattleInfoBox.HP_BACKGROUND_COLOR)
    val hpFullWidth = getObjectWidth - 107
    g2d.fillRect(100, 42, hpFullWidth, 10)
    g2d.setColor(BattleInfoBox.HP_FILL_COLOR)
    val drawHP = (pokemon.getCurrentStats.getHP - amount *
      (currentFrame.toDouble / HPBarAnimation.ANIMATION_FRAMES)) max 0
    val hpActualWidth = (hpFullWidth * drawHP / pokemon.getStandardStats.getHP).toInt
    g2d.fillRect(100, 42, hpActualWidth, 10)
    if(battleInfoBox.isHPDisplayed) {
      //TODO why does this font look slightly off during animation?
      g2d.setFont(BattleInfoBox.HP_FONT)
      val hpString = "%d/%d".format(drawHP.toInt, pokemon.getStandardStats.getHP)
      val hpWidth = g2d.getFontMetrics(BattleInfoBox.HP_FONT).stringWidth(hpString)
      g2d.setColor(BattleInfoBox.DEFAULT_BACKGROUND_COLOR)
      g2d.fillRect(BattleInfoBox.BASE_SIZE_X - hpWidth - 6, 54, hpWidth, 18)
      g2d.setColor(BattleInfoBox.DEFAULT_FONT_COLOR)
      g2d.drawString(hpString, BattleInfoBox.BASE_SIZE_X - hpWidth - 6, 68)
    }
    g2d.dispose()
    canvasImage
  }

  /** Returns the object's image, already scaled. This is to speed up rendering. */
  override def getPrescaledImage: Option[BufferedImage] = Some(getImage)

  /** Returns true if the animation is complete. */
  override def isAnimationComplete: Boolean = currentFrame >= HPBarAnimation.ANIMATION_FRAMES

  /** Progresses animations by one frame. Parent objects should call on all child objects they render. */
  override def advanceFrame(): Unit = if(running) currentFrame += 1
}
