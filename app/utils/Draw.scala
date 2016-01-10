package utils

import models.{Ranks, Player, RobinPlayer, SeriesPlayer}

import scala.collection.immutable.IndexedSeq

/**
  * @author Koen Van Loock
  * @version 1.0 9/01/2016 21:13
  */
object Draw {


  def drawRobin(sortedPlayers: List[SeriesPlayer], numberOfRobins: Int): List[List[RobinPlayer]] = {
    (0 until numberOfRobins).map { currentRobinNr =>
      sortedPlayers.zipWithIndex
        .flatMap {
          case (player: SeriesPlayer, index: Int) =>
            if (index % numberOfRobins == currentRobinNr) {
              Some(RobinPlayer(index, "", "", "", player.rank, 0, 0, 0, 0, 0, 0))
            } else {
              None
            }
          case _ => None
        }
    }.toList
  }

  def drawBracket() = {}


}
