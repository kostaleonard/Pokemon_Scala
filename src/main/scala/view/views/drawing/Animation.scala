package view.views.drawing

import view.views.BattleView

object Animation {
  val MOVE_ANIMATION_DRAW_WIDTH: Int = BattleView.BACKGROUND_IMAGE.getWidth(null) *
    BattleView.BACKGROUND_IMAGE_SCALE_FACTOR
  val MOVE_ANIMATION_DRAW_HEIGHT: Int = BattleView.BACKGROUND_IMAGE.getHeight(null) *
    BattleView.BACKGROUND_IMAGE_SCALE_FACTOR
}

trait Animation extends Drawable {
  protected var animationCallback: Option[() => Unit] = None
  protected var running = false

  /** Specifies code to execute after animation is complete. */
  def setAnimationCallback(f: Option[() => Unit]): Unit = animationCallback = f

  /** Executes the callback. */
  def makeAnimationCallback(): Unit = if(animationCallback.nonEmpty) animationCallback.get.apply()

  /** Begins the animation. */
  def start(): Unit = running = true

  /** Pauses the animation. */
  def stop(): Unit = running = false

  /** Returns true if the animation is complete. */
  def isAnimationComplete: Boolean

  /** Progresses animations by one frame. Parent objects should call on all child objects they render. */
  def advanceFrame(): Unit
}
