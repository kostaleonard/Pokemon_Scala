package model.board

sealed trait Terrain

case object Dirt extends Terrain
case object LowGrass extends Terrain
case object TallGrass extends Terrain
