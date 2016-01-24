package models


case class RobinPlayer(robinPlayerId: Option[Int], robinGroupId: Option[Int], seriesPlayerId: Int, rankValue: Int, robinNr: Int, wonMatches: Int, lostMatches: Int, wonSets: Int, lostSets: Int, wonPoints: Int, lostPoints: Int)
case class RobinMatch(robinMatchId: Option[Int], robinId: Int, matchId: Int)

case class RoundRobinGroup(robinId: Option[Int], seriesRoundId: Int){//, robinPlayers: List[RobinPlayer], robinMatches: List[SiteMatch]){


  //def complete(numberOfSetsToWin: Int, setTargetScore: Int): Boolean = robinMatches.forall( _.isComplete)
}


