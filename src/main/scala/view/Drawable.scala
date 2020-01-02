package view

import java.awt.image.BufferedImage

/** Represents an object that can be drawn. */
trait Drawable {
  /** This is the canvas on which the object's image is drawn. */
  protected val canvasImage: BufferedImage = new BufferedImage(getObjectWidth, getObjectHeight,
    BufferedImage.TYPE_INT_RGB)

  /** Returns the object's width. */
  def getObjectWidth: Int

  /** Returns the object's height. */
  def getObjectHeight: Int

  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  def getImage: BufferedImage
}
