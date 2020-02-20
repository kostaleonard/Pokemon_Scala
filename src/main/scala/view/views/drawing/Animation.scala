package view.views.drawing

trait Animation extends Drawable {
  protected var animationCallback: Option[() => Unit] = None
  /** Specifies code to execute after animation is complete. */
  def setAnimationCallback(f: Option[() => Unit]): Unit = animationCallback = f

  /** Executes the callback. */
  def makeAnimationCallback(): Unit = if(animationCallback.nonEmpty) animationCallback.get.apply()

  /** Returns true if the animation is complete. */
  def isAnimationComplete: Boolean

  /** Progresses animations by one frame. Parent objects should call on all child objects they render. */
  def advanceFrame(): Unit
}
