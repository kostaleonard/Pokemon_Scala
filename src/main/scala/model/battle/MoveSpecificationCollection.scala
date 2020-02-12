package model.battle

/** Represents the MoveSpecifications from before, during, and after the move. */
case class MoveSpecificationCollection(beforeMoveSpecs: Array[MoveSpecification],
                                       duringMoveSpecs: Array[MoveSpecification])
