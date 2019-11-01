package model.statuseffect.persistent

import model.statuseffect.StatusEffect

abstract class PersistentEffect extends StatusEffect {
  def doInitialAction: Unit

  def doTurnlyAction: Unit

  def doOutOfBattleAction: Unit
}
