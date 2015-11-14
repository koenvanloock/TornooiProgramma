package models


case class RobinPlayer(playerId: Long, firstname: String, lastname: String, club: String, rank: Rank, wonMatches: Int, lostMatches: Int, wonSets: Int, lostSets: Int, wonPoints: Int, lostPoints: Int)

case class RoundRobinGroup(robinId: Int, seriesRoundId: Int, robinPlayers: List[RobinPlayer], robinMatches: List[SiteMatch]){


  def complete(numberOfSetsToWin: Int, setTargetScore: Int): Boolean = robinMatches.forall( _.isComplete)
}
