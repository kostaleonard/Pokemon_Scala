package controller

import model.pokemon.exp.LevelTracker
import model.pokemon.species.Charmander

/** Runs the program and handles user input. Only displays information via stdout. */
object PrintController {

  /** Runs the program. */
  def main(args: Array[String]): Unit = {
    println("Hello world!")

    val p1 = new Charmander(LevelTracker.create(5))
    println(p1.getBaseStats.getStatsMap)
    println(p1.getIVStats.getStatsMap)
    p1.getIVStats.setStat("HP", 1)
    println(p1.getIVStats.getStatsMap)
    p1.getIVStats.incrementStat("HP")
    println(p1.getIVStats.getStatsMap)
    p1.getIVStats.decrementStat("HP")
    println(p1.getIVStats.getStatsMap)
    println(p1.getTypeArray.head.toString)
  }
}
