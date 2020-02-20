package view.views.drawing

import java.awt.image.BufferedImage

trait FrameByFrameAnimation extends Animation {
  protected var currentFrameIndex = 0

  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  override def getImage: BufferedImage = getFrames(currentFrameIndex)

  /** Returns the object's image, already scaled. This is to speed up rendering. */
  override def getPrescaledImage: Option[BufferedImage] = getPrescaledFrames match {
    case Some(arr) => Some(arr(currentFrameIndex))
    case None => None
  }

  /** Returns true if the animation is complete. */
  override def isAnimationComplete: Boolean = currentFrameIndex >= getFrames.length - 1

  /** Progresses animations by one frame. Parent objects should call on all child objects they render. */
  override def advanceFrame(): Unit = currentFrameIndex += 1

  /** Returns all of this Animation's images, in order. */
  def getFrames: Array[BufferedImage]

  /** Returns all of this Animation's images, in order. */
  def getPrescaledFrames: Option[Array[BufferedImage]]
}
