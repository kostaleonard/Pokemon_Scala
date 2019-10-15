package model.elementaltype

trait ElementalType {
  /** Returns the string representation of this object. */
  override def toString: String = this match{
    case NormalType => "Normal"
    case FireType => "Fire"
    case GrassType => "Grass"
    case WaterType => "Water"
    case _ => throw new UnsupportedOperationException("Unrecognized type.")
  }
}

case object NormalType extends ElementalType
case object FireType extends ElementalType
case object GrassType extends ElementalType
case object WaterType extends ElementalType
