package view.views.drawing.animations

import java.awt.{Color, Graphics2D}
import java.awt.image.BufferedImage

import model.battle.BattleInfoBox
import model.pokemon.Pokemon
import view.views.drawing.Animation

object HPBarAnimation {
  val ANIMATION_FRAMES = 120
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
    val hp_full_width = getObjectWidth - 107
    g2d.fillRect(100, 42, hp_full_width, 10)
    //g2d.setColor(BattleInfoBox.HP_FILL_COLOR)
    g2d.setColor(Color.MAGENTA)
    val drawHP = (pokemon.getCurrentStats.getHP - amount *
      (currentFrame.toDouble / HPBarAnimation.ANIMATION_FRAMES)) max 0
    val hp_actual_width = (hp_full_width * drawHP / pokemon.getStandardStats.getHP).toInt
    g2d.fillRect(100, 42, hp_actual_width, 10)
    if(battleInfoBox.isHPDisplayed) {
      g2d.setColor(BattleInfoBox.DEFAULT_FONT_COLOR)
      val hpString = "%d/%d".format(drawHP.toInt, pokemon.getStandardStats.getHP)
      val hpWidth = g2d.getFontMetrics(BattleInfoBox.HP_FONT).stringWidth(hpString)
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
