package model.elementaltype

sealed trait ElementalType {
  /** Returns the string representation of this object. */
  override def toString: String = this match{
    case NormalType => "Normal"
    case FireType => "Fire"
    case WaterType => "Water"
    case ElectricType => "Electric"
    case GrassType => "Grass"
    case IceType => "Ice"
    case FightingType => "Fighting"
    case PoisonType => "Poison"
    case GroundType => "Ground"
    case FlyingType => "Flying"
    case PsychicType => "Psychic"
    case BugType => "Bug"
    case RockType => "Rock"
    case GhostType => "Ghost"
    case DragonType => "Dragon"
    case DarkType => "Dark"
    case SteelType => "Steel"
    case _ => throw new UnsupportedOperationException("Unrecognized type.")
  }

  /** Returns the type effectiveness multiplier used when this type is used against another type. */
  def getTypeEffectiveness(otherType: ElementalType): Double = this match {
    case NormalType => otherType match {
      case RockType => 0.5
      case GhostType => 0.0
      case SteelType => 0.5
      case _ => 1.0
    }
    case FireType => otherType match {
      case FireType => 0.5
      case WaterType => 0.5
      case GrassType => 2.0
      case IceType => 2.0
      case BugType => 2.0
      case RockType => 0.5
      case DragonType => 0.5
      case SteelType => 2.0
      case _ => 1.0
    }
    case WaterType => otherType match {
      case FireType => 2.0
      case WaterType => 0.5
      case GrassType => 0.5
      case GroundType => 2.0
      case RockType => 2.0
      case DragonType => 0.5
      case _ => 1.0
    }
    case ElectricType => otherType match {
      case WaterType => 2.0
      case ElectricType => 0.5
      case GrassType => 0.5
      case GroundType => 0.0
      case FlyingType => 2.0
      case DragonType => 0.5
      case _ => 1.0
    }
    case GrassType => otherType match {
      case FireType => 0.5
      case WaterType => 2.0
      case GrassType => 0.5
      case PoisonType => 0.5
      case GroundType => 2.0
      case FlyingType => 0.5
      case BugType => 0.5
      case RockType => 2.0
      case DragonType => 0.5
      case SteelType => 0.5
      case _ => 1.0
    }
    case IceType => otherType match {
      case FireType => 0.5
      case WaterType => 0.5
      case GrassType => 2.0
      case IceType => 0.5
      case GroundType => 2.0
      case FlyingType => 2.0
      case DragonType => 2.0
      case SteelType => 0.5
      case _ => 1.0
    }
    case FightingType => otherType match {
      case NormalType => 2.0
      case IceType => 2.0
      case PoisonType => 0.5
      case FlyingType => 0.5
      case PsychicType => 0.5
      case BugType => 0.5
      case RockType => 2.0
      case GhostType => 0.0
      case DarkType => 2.0
      case SteelType => 2.0
      case _ => 1.0
    }
    case PoisonType => otherType match {
      case GrassType => 2.0
      case PoisonType => 0.5
      case GroundType => 0.5
      case RockType => 0.5
      case GhostType => 0.5
      case SteelType => 0.0
      case _ => 1.0
    }
    case GroundType => otherType match {
      case FireType => 2.0
      case ElectricType => 2.0
      case GrassType => 0.5
      case PoisonType => 2.0
      case FlyingType => 0.0
      case BugType => 0.5
      case RockType => 2.0
      case SteelType => 2.0
      case _ => 1.0
    }
    case FlyingType => otherType match {
      case ElectricType => 0.5
      case GrassType => 2.0
      case FightingType => 2.0
      case BugType => 2.0
      case RockType => 0.5
      case SteelType => 0.5
      case _ => 1.0
    }
    case PsychicType => otherType match {
      case FightingType => 2.0
      case PoisonType => 2.0
      case PsychicType => 0.5
      case DarkType => 0.0
      case SteelType => 0.5
      case _ => 1.0
    }
    case BugType => otherType match {
      case FireType => 0.5
      case GrassType => 2.0
      case FightingType => 0.5
      case PoisonType => 0.5
      case FlyingType => 0.5
      case PsychicType => 2.0
      case GhostType => 0.5
      case DarkType => 2.0
      case SteelType => 0.5
      case _ => 1.0
    }
    case RockType => otherType match {
      case FireType => 2.0
      case IceType => 2.0
      case FightingType => 0.5
      case GroundType => 0.5
      case FlyingType => 2.0
      case BugType => 2.0
      case SteelType => 0.5
      case _ => 1.0
    }
    case GhostType => otherType match {
      case NormalType => 0.0
      case PsychicType => 2.0
      case GhostType => 2.0
      case DarkType => 0.5
      case _ => 1.0
    }
    case DragonType => otherType match {
      case DragonType => 2.0
      case SteelType => 0.5
      case _ => 1.0
    }
    case DarkType => otherType match {
      case FightingType => 0.5
      case PsychicType => 2.0
      case GhostType => 2.0
      case DarkType => 0.5
      case _ => 1.0
    }
    case SteelType => otherType match {
      case FireType => 0.5
      case WaterType => 0.5
      case ElectricType => 0.5
      case IceType => 2.0
      case RockType => 2.0
      case SteelType => 0.5
      case _ => 1.0
    }
    case _ => throw new UnsupportedOperationException("Unrecognized type.")
  }
}

case object NormalType extends ElementalType
case object FireType extends ElementalType
case object WaterType extends ElementalType
case object ElectricType extends ElementalType
case object GrassType extends ElementalType
case object IceType extends ElementalType
case object FightingType extends ElementalType
case object PoisonType extends ElementalType
case object GroundType extends ElementalType
case object FlyingType extends ElementalType
case object PsychicType extends ElementalType
case object BugType extends ElementalType
case object RockType extends ElementalType
case object GhostType extends ElementalType
case object DragonType extends ElementalType
case object DarkType extends ElementalType
case object SteelType extends ElementalType

