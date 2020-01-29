package view.frame

import java.awt.event._
import java.awt.image.BufferedImage
import java.util.concurrent.ScheduledThreadPoolExecutor

import controller.{Controller, KeyPressManager}
import javax.swing.{JFrame, Timer}

object ViewFrame{
  val SCHEDULED_THREAD_POOL_EXECUTOR_NUM_THREADS = 3
  val SCHEDULED_THREAD_POOL_EXECUTOR_INITIAL_DELAY = 0
  val SHOW_FPS_TIMER: Boolean = false
  val SHOW_ADVANCE_FRAME_TIMER: Boolean = false
}

class ViewFrame(viewManager: ViewManager, controller: Controller) extends JFrame {
  protected val keyPressManager = new KeyPressManager(controller)
  protected val mainPanel = new ImageRenderPanel
  protected val repaintTimer = new Timer(ViewManager.MILLISECONDS_PER_SECOND / ViewManager.FRAMES_PER_SECOND,
    new RepaintListener)
  protected val frameAdvanceTimer = new Timer(ViewManager.MILLISECONDS_PER_SECOND / ViewManager.FRAMES_PER_SECOND,
    new FrameAdvanceTimer)
  //TODO I have no idea how the keyHeldExecutor works anymore.
  protected val keyHeldExecutor = new ScheduledThreadPoolExecutor(ViewFrame.SCHEDULED_THREAD_POOL_EXECUTOR_NUM_THREADS)
  setVisible(false)

  /** Returns the frame's KeyPressManager. */
  def getKeyPressManager: KeyPressManager = keyPressManager

  /** Sets up the frame. */
  def setup(): Unit = {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    //setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
    addWindowListener(new CustomWindowListener)
    addComponentListener(new CustomComponentListener)
    setSize(ViewManager.DEFAULT_FRAME_WIDTH, ViewManager.DEFAULT_FRAME_HEIGHT)
    //TODO maximize the frame by default once testing is completed.
    //setExtendedState(java.awt.Frame.MAXIMIZED_BOTH)
    mainPanel.setSize(getWidth, getHeight)
    //TODO frame decoration is throwing off graphics calculations slightly. Give user these buttons organically, then uncomment below.
    //setUndecorated(true)
    setVisible(true)
    setFocusable(true)
    add(mainPanel)
    addKeyListener(keyPressManager)
    setupDrawTimers()
  }

  /** Starts the repaint and frame timers. */
  def setupDrawTimers(): Unit = {
    repaintTimer.start()
    frameAdvanceTimer.start()
  }

  /** Displays the given image on the frame. */
  def renderImage(bufferedImage: BufferedImage): Unit = {
    mainPanel.setCurrentImage(Some(bufferedImage))
    mainPanel.repaint()
  }

  class RepaintListener extends ActionListener {
    protected var referenceTime: Long = System.nanoTime()
    protected var framesProcessed = 0
    /** Called every time the timer expires. */
    override def actionPerformed(e: ActionEvent): Unit = {
      //TODO this is the slow part.
      viewManager.repaint()
      framesProcessed += 1
      val seconds = (System.nanoTime() - referenceTime) / 1e9
      if(seconds > 1.0){
        if(ViewFrame.SHOW_FPS_TIMER) println("FPS: %f".format(framesProcessed / seconds))
        framesProcessed = 0
        referenceTime = System.nanoTime()
      }
    }
  }

  class FrameAdvanceTimer extends ActionListener {
    protected var referenceTime: Long = System.nanoTime()
    protected var framesProcessed = 0
    /** Called every time the timer expires. */
    override def actionPerformed(e: ActionEvent): Unit = {
      viewManager.getCurrentView.advanceFrame()
      framesProcessed += 1
      val seconds = (System.nanoTime() - referenceTime) / 1e9
      if(seconds > 1.0){
        if(ViewFrame.SHOW_ADVANCE_FRAME_TIMER) println("Frame advance FPS: %f".format(framesProcessed / seconds))
        framesProcessed = 0
        referenceTime = System.nanoTime()
      }
    }
  }

  //TODO is this necessary?
  class CustomWindowListener extends WindowListener {
    /** Called when the window is closed. */
    override def windowClosed(e: WindowEvent): Unit = {
      repaintTimer.stop()
      frameAdvanceTimer.stop()
      keyPressManager.getKeyTimer.stop()
      keyHeldExecutor.shutdown()
      controller.exitGame()
    }

    /** Called when the window is activated. */
    override def windowActivated(e: WindowEvent): Unit = {}

    /** Called when the window is iconified. */
    override def windowIconified(e: WindowEvent): Unit = {}

    //TODO either this or windowClosed should just be a super call.
    /** Called when the window is closing. */
    override def windowClosing(e: WindowEvent): Unit = {
      repaintTimer.stop()
      frameAdvanceTimer.stop()
      keyPressManager.getKeyTimer.stop()
      keyHeldExecutor.shutdown()
      controller.exitGame()
    }

    /** Called when the window is deactivated. */
    override def windowDeactivated(e: WindowEvent): Unit = {}

    /** Called when the window is deiconified. */
    override def windowDeiconified(e: WindowEvent): Unit = {}

    /** Called when the window is opened. */
    override def windowOpened(e: WindowEvent): Unit = {}
  }

  //TODO is this necessary?
  class CustomComponentListener extends ComponentListener {
    /** Called when the component is hidden. */
    override def componentHidden(e: ComponentEvent): Unit = {}

    /** Called when the component is moved. */
    override def componentMoved(e: ComponentEvent): Unit = {}

    /** Called when the component is shown. */
    override def componentShown(e: ComponentEvent): Unit = {}

    /** Called when the component is resized. */
    override def componentResized(e: ComponentEvent): Unit = {
      mainPanel.setSize(getWidth, getHeight)
      mainPanel.repaint()
    }
  }
}
