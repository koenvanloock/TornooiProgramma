package models


case class RobinPlayer(robinPlayerId: Option[String], robinGroupId: Option[String], seriesPlayerId: String, rankValue: Int, robinNr: Int, wonMatches: Int, lostMatches: Int, wonSets: Int, lostSets: Int, wonPoints: Int, lostPoints: Int)
case class RobinMatch(robinMatchId: Option[String], robinId: String, matchId: String)

case class RoundRobinGroup(robinId: Option[String], seriesRoundId: String){//, robinPlayers: List[RobinPlayer], robinMatches: List[SiteMatch]){


  //def complete(numberOfSetsToWin: Int, setTargetScore: Int): Boolean = robinMatches.forall( _.isComplete)
}


