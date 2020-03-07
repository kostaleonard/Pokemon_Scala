package view.views.drawing.animations

import java.awt.{Color, Graphics2D, Image}
import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO
import view.View
import view.views.drawing.Animation

object EmberAnimation {
  val ANIMATION_FRAMES = 40
  val FIRE_WIDTH = 80
  val FIRE_HEIGHT = 80
  val FIRE_IMAGE_1: BufferedImage = ImageIO.read(new File(View.getSourcePath(
    "sprites/moves/EMBER/flame1.png")))
  val FIRE_IMAGE_2: BufferedImage = ImageIO.read(new File(View.getSourcePath(
    "sprites/moves/EMBER/flame2.png")))
}

class EmberAnimation(callback: Option[() => Unit])
  extends Animation {
  setAnimationCallback(callback)
  protected var canvasImage: BufferedImage = new BufferedImage(getObjectWidth, getObjectHeight,
    BufferedImage.TYPE_INT_ARGB)
  protected var currentFrame = 0

  /** Returns the object's width. */
  override def getObjectWidth: Int = Animation.MOVE_ANIMATION_DRAW_WIDTH

  /** Returns the object's height. */
  override def getObjectHeight: Int = Animation.MOVE_ANIMATION_DRAW_HEIGHT

  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  override def getImage: BufferedImage = {
    canvasImage = new BufferedImage(getObjectWidth, getObjectHeight, BufferedImage.TYPE_INT_ARGB)
    val g2d = canvasImage.getGraphics.asInstanceOf[Graphics2D]
    drawOffsetX = currentFrame * 3
    drawOffsetY = currentFrame / 2
    val fireImage = if((currentFrame / 10) % 2 == 0) EmberAnimation.FIRE_IMAGE_1 else EmberAnimation.FIRE_IMAGE_2
    g2d.drawImage(fireImage, 625 + drawOffsetX, 210 - drawOffsetY, null)
    g2d.dispose()
    canvasImage
  }

  /** Returns the object's image, already scaled. This is to speed up rendering. */
  override def getPrescaledImage: Option[BufferedImage] = Some(getImage)

  /** Returns true if the animation is complete. */
  override def isAnimationComplete: Boolean = currentFrame >= EmberAnimation.ANIMATION_FRAMES

  /** Progresses animations by one frame. Parent objects should call on all child objects they render. */
  override def advanceFrame(): Unit = if(running) currentFrame += 1
}
