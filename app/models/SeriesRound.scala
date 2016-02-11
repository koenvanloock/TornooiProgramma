package models

sealed trait SeriesRound

case class SiteBracketRound(seriesRoundId: Option[String], numberOfBracketRounds: Int, roundType: String, seriesId: String, roundNr: Int) extends SeriesRound
case class RobinRound(seriesRoundId: Option[String], numberOfRobins: Int, roundType: String,seriesId: String, roundNr: Int) extends SeriesRound

case class GenericSeriesRound(seriesRoundId: Option[String], numberOfBracketRounds: Option[Int], numberOfRobins: Option[Int], roundType: String, seriesId: String, roundNr: Int)