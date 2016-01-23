package utils

import models.{RobinRound, RoundRobinGroup, RobinPlayer, SeriesPlayer}


object Draw {


  def splitPlayersInGroups(sortedPlayers: List[SeriesPlayer], numberOfRobins: Int): List[List[RobinPlayer]] = {
    (0 until numberOfRobins)
      .map { currentRobinNr =>
        sortedPlayers.zipWithIndex
          .flatMap {
            case (player: SeriesPlayer, index: Int) =>
              if (index % numberOfRobins == currentRobinNr) {
                Some(RobinPlayer(None, None, player.seriesPlayerId.get, player.rank.value, (index/numberOfRobins) +1 , 0, 0, 0, 0, 0, 0))
              } else {
                None
              }
            case _ => None
          }
      }.toList
  }


  def drawBracket() = {}


}
