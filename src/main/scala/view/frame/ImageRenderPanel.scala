package view.frame

import java.awt.{Color, Graphics}
import java.awt.image.BufferedImage

import javax.swing.JPanel

class ImageRenderPanel extends JPanel {
  protected var currentImage: Option[BufferedImage] = None
  protected var backgroundColor: Color = Color.RED.darker //TODO change background color. It is dark red for testing.
  setFocusable(true)
  setDoubleBuffered(true)

  /** Gets the current image. */
  def getCurrentImage: Option[BufferedImage] = currentImage

  /** Sets the current image to the given value. */
  def setCurrentImage(opt: Option[BufferedImage]): Unit = currentImage = opt

  /** Draws the image. */
  def renderImage(g: Graphics): Unit = {
    g.setColor(backgroundColor)
    g.fillRect(0, 0, getWidth, getHeight)
    //TODO this currently does not force the 16:9 aspect ratio. Consider changing.
    currentImage match{
      case Some(bufferedImage) => g.drawImage(bufferedImage,
        0, 0, getWidth, getHeight,
        0, 0, bufferedImage.getWidth, bufferedImage.getHeight,
        null)
      case None => ;
    }
  }

  /** Draws the image on the panel. */
  override def paintComponent(g: Graphics): Unit = {
    //TODO there may be a way to speed up graphics by avoiding the use of paintComponent.
    super.paintComponent(g)
    renderImage(g)
  }
}
