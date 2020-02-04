package model.battle

//TODO this could be generalized into a DialogueMessage so that it can be used in the OverworldView too.
case class BattleMessage(message: List[String], callbackOnMessageComplete: () => Unit) {
  /** Returns true if there are linesPerPage or fewer elements in message. */
  def isOnLastPage(linesPerPage: Int): Boolean = message.length <= linesPerPage
}
