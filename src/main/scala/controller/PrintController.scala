package controller

import model.battle.Battle
import model.actor.PlayerCharacter
import model.party.Party
import model.pokemon.exp.LevelTracker
import model.pokemon.species.Charmander

import scala.collection.mutable.ListBuffer

/** Runs the program and handles user input. Only displays information via stdout. */
object PrintController {
  /** Runs the program. */
  def main(args: Array[String]): Unit = {
    println("Hello world!")
    println(System.getProperty("user.dir"))

    testBattle()
  }

  /** Prints some Pokemon stats. */
  protected def testStats(): Unit = {
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

  /** Tests battle functionality. */
  protected def testBattle(): Unit = {
    val playerPokemon = new Charmander(LevelTracker.create(8))
    val opponentPokemon = new Charmander(LevelTracker.create(7))
    val playerCharacter = new PlayerCharacter(new Party(ListBuffer(playerPokemon)))
    val battle = new Battle(playerCharacter, None, Some(opponentPokemon))
    playerPokemon.getMoveList.getMoves.foreach(println)
    //TODO this test code got broken in a refactor.
    /*
    while(!battle.isOver){
      println("Choose a move.")
      playerPokemon.getMoveList.getMoves.zipWithIndex.foreach(tup => println("%d.%s".format(tup._2, tup._1.getName)))
      val chosenIndex = scala.io.StdIn.readInt()
      //battle.makePlayerMove(playerPokemon.getMoveList.getUsableMoves.head)
      battle.getPlayerMoveSpecifications(playerPokemon.getMoveList.getMoves(chosenIndex))
      println("Opponent: %d/%d".format(opponentPokemon.getCurrentStats.getHP, opponentPokemon.getStandardStats.getHP))
      if(!opponentPokemon.isKO) battle.getOpponentMoveSpecifications
      println("Player: %d/%d".format(playerPokemon.getCurrentStats.getHP, playerPokemon.getStandardStats.getHP))
      println
    }
    */
  }
}
