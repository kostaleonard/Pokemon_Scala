package controller

import model.battle.Battle
import model.pokemon.exp.LevelTracker
import model.pokemon.species.Charmander

/** Runs the program and handles user input. Only displays information via stdout. */
object PrintController {

  /** Runs the program. */
  def main(args: Array[String]): Unit = {
    println("Hello world!")

    testBattle
  }

  /** Prints some Pokemon stats. */
  protected def testStats: Unit = {
    val p1 = new Charmander(LevelTracker.create(5), true)
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

  /** Tests battle functionality. */
  protected def testBattle: Unit = {
    val playerPokemon = new Charmander(LevelTracker.create(5), false)
    val opponentPokemon = new Charmander(LevelTracker.create(4), true)
    val battle = new Battle(playerPokemon, opponentPokemon)
    playerPokemon.getMoveList.getMoves.foreach(println)

    (1 to 10).foreach { i =>
      println("Choose a move.")
      playerPokemon.getMoveList.zipWithIndex.foreach((move, i) => println("%d.%s".format(i, move.getName)))
      val chosenIndex = readInt
      //battle.makePlayerMove(playerPokemon.getMoveList.getUsableMoves.head)
      battle.makePlayerMove(playerPokemon.getMoveList(chosenIndex))
      println("Opponent: %d/%d".format(opponentPokemon.getCurrentStats.getHP, opponentPokemon.getStandardStats.getHP))
      battle.makeOpponentMove
      println("Player: %d/%d".format(playerPokemon.getCurrentStats.getHP, playerPokemon.getStandardStats.getHP))
    }
  }
}
