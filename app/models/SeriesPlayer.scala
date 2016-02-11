package models

case class SeriesPlayer(seriesPlayerId: Option[String], playerId: String, rank: Rank, wonMatches: Int=0, lostMatches: Int=0, wonSets: Int=0, lostSets: Int=0, wonPoints: Int=0, lostPoints: Int=0, seriesId: String)

