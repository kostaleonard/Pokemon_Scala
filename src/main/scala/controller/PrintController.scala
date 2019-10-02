package controller

import model.pokemon.species.Charmander

/** Runs the program and handles user input. Only displays information via stdout. */
object PrintController {

  /** Runs the program. */
  def main(args: Array[String]): Unit = {
    println("Hello world!")

    val p1 = new Charmander
    println(p1.getBaseStats)
    println(p1.getIVStats)
  }
}
