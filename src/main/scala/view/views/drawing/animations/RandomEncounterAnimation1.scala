package view.views.drawing.animations

import java.awt.{Color, Graphics2D}
import java.awt.image.BufferedImage

import view.View
import view.views.drawing.Animation

object RandomEncounterAnimation1 {
  val ANIMATION_FRAMES = 130
}

class RandomEncounterAnimation1(callback: Option[() => Unit]) extends Animation {
  setAnimationCallback(callback)
  protected var canvasImage: BufferedImage = new BufferedImage(getObjectWidth, getObjectHeight,
    BufferedImage.TYPE_INT_ARGB)
  protected var currentFrame = 0

  /** Returns the object's width. */
  override def getObjectWidth: Int = View.FRAME_DESIGN_WIDTH

  /** Returns the object's height. */
  override def getObjectHeight: Int = View.FRAME_DESIGN_HEIGHT

  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  override def getImage: BufferedImage = {
    val g2d = canvasImage.getGraphics.asInstanceOf[Graphics2D]
    g2d.setColor(Color.BLACK)
    val offset = currentFrame * 4
    g2d.fillRect(0, 0, offset, getObjectHeight)
    g2d.fillRect(getObjectWidth - offset, 0, offset, getObjectHeight)
    g2d.dispose()
    canvasImage
  }

  /** Returns the object's image, already scaled. This is to speed up rendering. */
  override def getPrescaledImage: Option[BufferedImage] = Some(getImage)

  /** Returns true if the animation is complete. */
  override def isAnimationComplete: Boolean = currentFrame >= RandomEncounterAnimation1.ANIMATION_FRAMES

  /** Progresses animations by one frame. Parent objects should call on all child objects they render. */
  override def advanceFrame(): Unit = if(running) currentFrame += 1
}
