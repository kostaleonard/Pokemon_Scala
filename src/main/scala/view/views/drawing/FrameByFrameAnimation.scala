package view.views.drawing

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO

object FrameByFrameAnimation {
  /** Returns the frames from the given directory. Throws an error if the path is not a directory, if it is an empty
    * directory, or if it does not exist. Files are sorted by name. */
  def getFramesFromSource(path: String): Array[BufferedImage] = {
    val d = new File(path)
    if (!d.exists) throw new UnsupportedOperationException("File %s does not exist.".format(path))
    if (!d.isDirectory) throw new UnsupportedOperationException("File %s is not a directory.".format(path))
    if (d.listFiles.isEmpty) throw new UnsupportedOperationException("Directory %s is empty.".format(path))
    d.listFiles.filter(_.isFile).sortBy(_.getName).map(ImageIO.read)
  }

  //TODO this could consume a lot of memory.
  /** Returns the frames from the given directory, with the repeat array defining how many times each image in the
    * directory should be repeated. */
  def getFramesFromSourceWithDuration(path: String, repeat: Array[Int]): Array[BufferedImage] = {
    val rawFrames = getFramesFromSource(path)
    if(rawFrames.length != repeat.length) throw new UnsupportedOperationException("Repeat array length must match " +
      "the number of images in the directory.")
    rawFrames.zip(repeat).flatMap(pair => Array.fill(pair._2)(pair._1))
  }

  /** Returns the frames scaled to the given width and height. */
  def scaleFrames(frames: Array[BufferedImage], targetWidth: Int, targetHeight: Int): Array[BufferedImage] =
    frames.map{ frame =>
      val bufferedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB)
      val g2d = bufferedImage.getGraphics.asInstanceOf[Graphics2D]
      g2d.drawImage(frame, 0, 0, targetWidth, targetHeight, null)
      g2d.dispose()
      bufferedImage
    }
}

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
