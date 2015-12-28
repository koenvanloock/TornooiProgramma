package models

sealed trait SeriesRound

case class SiteBracketRound(seriesRoundId: Option[Int], numberOfBracketRounds: Int, roundType: String, seriesId: Int, roundNr: Int) extends SeriesRound
case class RobinRound(seriesRoundId: Option[Int], numberOfRobins: Int, roundType: String,seriesId: Int, roundNr: Int) extends SeriesRound

case class GenericSeriesRound(seriesRoundId: Option[Int], numberOfBracketRounds: Option[Int], numberOfRobins: Option[Int], roundType: String, seriesId: Int, roundNr: Int)