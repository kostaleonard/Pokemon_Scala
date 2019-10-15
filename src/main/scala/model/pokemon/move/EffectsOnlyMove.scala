package model.pokemon.move

abstract class EffectsOnlyMove extends Move {
  /** Returns the move's power, or None if not applicable. */
  override def getPower: Option[Int] = None


}
