package view.views

import java.awt.{Color, Graphics2D, Point}
import java.awt.image.BufferedImage

import controller.{KeyMappings, SwitchViews}
import model.Model
import model.board.{Board, Location}
import view.View

class OverworldView(override protected val model: Model) extends View(model) {
  /** Returns the offset for centering the player. */
  def getCenteringOffset: Point = {
    val loc = model.getPlayerLocation.get
    new Point(
      ((loc.col + 0.5) * Board.TILE_SIZE).toInt - View.FRAME_DESIGN_WIDTH / 2,
      ((loc.row + 0.5) * Board.TILE_SIZE).toInt - View.FRAME_DESIGN_HEIGHT / 2
    )
  }

  /** The action taken when a key is pressed and the View is in focus. */
  override def keyPressed(keyCode: Int): Unit = keyCode match {
    case KeyMappings.DOWN_KEY =>
      val playerLoc = model.getPlayerLocation
      if(playerLoc.isEmpty) throw new UnsupportedOperationException("Cannot move player without location.")
      model.movePlayer(Location(playerLoc.get.row + 1, playerLoc.get.col))
    case KeyMappings.UP_KEY =>
      val playerLoc = model.getPlayerLocation
      if(playerLoc.isEmpty) throw new UnsupportedOperationException("Cannot move player without location.")
      model.movePlayer(Location(playerLoc.get.row - 1, playerLoc.get.col))
    case KeyMappings.LEFT_KEY =>
      val playerLoc = model.getPlayerLocation
      if(playerLoc.isEmpty) throw new UnsupportedOperationException("Cannot move player without location.")
      model.movePlayer(Location(playerLoc.get.row, playerLoc.get.col - 1))
    case KeyMappings.RIGHT_KEY =>
      val playerLoc = model.getPlayerLocation
      if(playerLoc.isEmpty) throw new UnsupportedOperationException("Cannot move player without location.")
      model.movePlayer(Location(playerLoc.get.row, playerLoc.get.col + 1))
    case _ => ;
  }

  /** The action taken when a key is released and the View is in focus. */
  override def keyReleased(keyCode: Int): Unit = {}

  /** The action taken when a key is typed and the View is in focus. */
  override def keyTyped(keyCode: Int): Unit = {}

  /** The action taken when a key is held and the View is in focus. */
  override def keyHeld(keyCode: Int): Unit = {}

  /** Returns the object's image, which should be drawn on the canvasImage. This image may be scaled later. */
  override def getImage: BufferedImage = {
    val g2d = canvasImage.getGraphics.asInstanceOf[Graphics2D]
    val boardImage = model.getCurrentBoard.getImage
    val centeringOffset = getCenteringOffset
    g2d.setColor(Color.BLACK)
    g2d.fillRect(0, 0, boardImage.getWidth, boardImage.getHeight)
    g2d.drawImage(boardImage, -centeringOffset.x, -centeringOffset.y, boardImage.getWidth, boardImage.getHeight,
      null)
    g2d.dispose()
    canvasImage
  }

  /** Progresses animations by one frame. Parent objects should call on all child objects they render. */
  override def advanceFrame(): Unit = model.getCurrentBoard.advanceFrame()
}
