package view.views.drawing

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO

//TODO get rid of this class.
object DirectoryAnimation {
  /** Returns the frames from the given directory. Throws an error if the path is not a directory, if it is an empty
    * directory, or if it does not exist. Files are sorted by name. */
  def getFramesFromSource(path: String): Array[BufferedImage] = {
    val d = new File(path)
    if (!d.exists) throw new UnsupportedOperationException("File %s does not exist.".format(path))
    if (!d.isDirectory) throw new UnsupportedOperationException("File %s is not a directory.".format(path))
    if (d.listFiles.isEmpty) throw new UnsupportedOperationException("Directory %s is empty.".format(path))
    d.listFiles.filter(_.isFile).sortBy(_.getName).map(ImageIO.read)
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

class DirectoryAnimation(path: String, targetWidth: Int, targetHeight: Int, callback: Option[() => Unit])
  extends FrameByFrameAnimation {
  protected val frames: Array[BufferedImage] = DirectoryAnimation.getFramesFromSource(path)
  protected val prescaledFrames: Option[Array[BufferedImage]] = Some(
    DirectoryAnimation.scaleFrames(frames, targetWidth, targetHeight))

  /** Returns the object's width. */
  override def getObjectWidth: Int = targetWidth

  /** Returns the object's height. */
  override def getObjectHeight: Int = targetHeight

  /** Returns all of this Animation's images, in order. */
  override def getFrames: Array[BufferedImage] = frames

  /** Returns all of this Animation's images, in order. */
  override def getPrescaledFrames: Option[Array[BufferedImage]] = prescaledFrames
}
