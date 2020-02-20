package view.views.drawing

import java.awt.image.BufferedImage

/** Represents an object that can be drawn. */
trait Drawable {
  protected var drawOffsetX = 0
  protected var drawOffsetY = 0

  /** Returns the Character's x offset for drawing. */
  def getDrawOffsetX: Int = drawOffsetX

  /** Returns the Character's y offset for drawing. */
  def getDrawOffsetY: Int = drawOffsetY

  /** Sets the Character's x offset for drawing. */
  def setDrawOffsetX(off: Int): Unit = drawOffsetX = off

  /** Sets the Character's y offset for drawing. */
  def setDrawOffsetY(off: Int): Unit = drawOffsetY = off

  /** Returns the object's width. */
  def getObjectWidth: Int

  /** Returns the object's height. */
  def getObjectHeight: Int

  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  def getImage: BufferedImage

  /** Returns the object's image, already scaled. This is to speed up rendering. */
  def getPrescaledImage: Option[BufferedImage]
}
