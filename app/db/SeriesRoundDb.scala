package db

import controllers.{SeriesRound, SiteBracketRound, RobinRound}
import play.api.db.DB
import anorm._
import play.api.Play.current

object SeriesRoundDb {

    def parseBracket(row: Row): Option[SeriesRound] = for{
    roundNr <- row[Option[Int]]("roundNr")
    seriesId <- row[Option[Int]]("seriesId")
    numberOfBracketRounds <- row[Option[Int]]("numberOfBracketRounds")
  }yield SiteBracketRound(row[Option[Int]]("tournamentRoundId"), numberOfBracketRounds,"B", roundNr)

  def parseRobin(row: Row): Option[SeriesRound] = for{
    roundNr <- row[Option[Int]]("roundNr")
    seriesId <- row[Option[Int]]("seriesId")
    numberOfRobinRounds <- row[Option[Int]]("numberOfRobins")
  }yield RobinRound(row[Option[Int]]("tournamentRoundId"), numberOfRobinRounds,"B", roundNr)


  def parseRoundFromRow(row: Row): Option[SeriesRound] = {
    row[String]("roundType") match {
      case "B" => parseBracket(row)
      case "R" => parseRobin(row)
      case _ => None
    }

  }
  def getSeriesRound(roundId: Int): Option[SeriesRound] = DB.withConnection{ implicit conn =>
    val row = SQL("""SELECT * FROM tournamentRounds WHERE tournamentRoundId = {roundId}""").on("roundId" -> roundId).apply().headOption
    row.map(parseRoundFromRow).flatten
  }

  def insertRobinRound(robinRound: RobinRound) = DB.withConnection{ implicit conn =>
      SQL(
        """INSERT INTO tournamentRounds(roundName, roundNr, seriesId, roundType, numberOfRobins)
          |VALUES({roundNr}{seriesId}{roundType},{numberOfRobins})""".stripMargin)
        .on(
          "roundNr"-> robinRound.roundNr,
          "seriesId"->robinRound.seriesRoundId,
          "roundType"-> robinRound.roundType,
          "numberOfRobins" -> robinRound.numberOfRobins).executeInsert()
  }

  def insertBracket(siteBracketRound: SiteBracketRound) = DB.withConnection{ implicit conn =>
    SQL(
      """INSERT INTO tournamentRounds(roundName, roundNr, seriesId, roundType)
        |VALUES({roundNr}{seriesId}{roundType})""".stripMargin)
      .on(
        "roundNr"-> siteBracketRound.roundNr,
        "seriesId"->siteBracketRound.seriesRoundId,
        "roundType"-> siteBracketRound.roundType,
        "numberOfBracketRounds"-> siteBracketRound.numberOfBracketRounds).executeInsert()

  }
}
