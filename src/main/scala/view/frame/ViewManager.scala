package view.frame

import java.awt.image.BufferedImage

import controller.Controller
import view.View

object ViewManager{
  val UNSUPPORTED_VIEW_OPERATION_EXCEPTION_MESSAGE: String = "The current View is not designed to render this " +
    "screen; you must switch the current View."
  val DEFAULT_FRAME_WIDTH = View.FRAME_DESIGN_WIDTH
  val DEFAULT_FRAME_HEIGHT = View.FRAME_DESIGN_HEIGHT
  val FRAMES_PER_SECOND = 60
  val KEY_EVENTS_PER_SECOND = 100
  val HELD_KEY_EVENTS_PER_KEY_ACTION = 10
  val MILLISECONDS_PER_SECOND = 1000
}

class ViewManager(initialView: View, controller: Controller){
  protected var currentView: View = initialView
  //TODO is lastDrawFunction necessary?
  //protected var lastDrawFunction: ()=>Unit = {()=>;}
  protected val frame = new ViewFrame(this, controller)
  setCurrentView(initialView)
  setupFrame()

  /** Returns the current view. */
  def getCurrentView: View = currentView

  /** Sets the current view to the specified value. */
  def setCurrentView(newView: View): Unit = {
    //TODO do we need the keyPressManager here?
    //currentView.setKeyPressManager(None)
    currentView = newView
    //currentView.setKeyPressManager(Some(frame.getKeyPressManager))
  }

  /** Redraws the frame's contents. */
  def repaint(): Unit = {
      //lastDrawFunction()
      showView()
  }

  /** Displays the given image on the frame. */
  protected def renderImage(bufferedImage: BufferedImage): Unit = frame.renderImage(bufferedImage)

  /** Prepares the frame to display the view. */
  protected def setupFrame(): Unit = frame.setup

  /** Displays the current view. */
  def showView(): Unit = renderImage(currentView.getImage)
}

case class UnsupportedViewOperationException(private val message: String =
                                             ViewManager.UNSUPPORTED_VIEW_OPERATION_EXCEPTION_MESSAGE,
                                             private val cause: Throwable = None.orNull)
  extends Exception(message, cause)
