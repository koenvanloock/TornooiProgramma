package db

import controllers.SeriesPlayer
import play.api.db.DB
import anorm._
import utils.RankConverter
import play.api.Play.current

object SeriesPlayerDb {
  def insertSeriesPlayer(playerToInsert: SeriesPlayer, seriesId: Long) = DB.withConnection{ implicit conn =>
    SQL(
      """INSERT INTO seriesPlayers(firstname, lastname, club, rank, seriesId)
        |VALUES({firstname},{lastname},{club},{rank}, {seriesId})""".stripMargin).on(
        "firstname"-> playerToInsert.firstname,
        "lastname"-> playerToInsert.lastname,
        "club"-> playerToInsert.club,
        "rank"->playerToInsert.rank.value,
        "seriesId" -> seriesId).executeInsert()
  }

  def updateSeriesPlayerGeneral(playerToUpdate: SeriesPlayer) = DB.withConnection{ implicit conn =>
    SQL(
      """UPDATE seriesPlayers
        |SET firstname = {firstname}, lastname = {lastname}, club = {club}, rank = {rank}
        |WHERE seriesPlayerId = {seriesPlayerId}""".stripMargin).on(
        "firstname"-> playerToUpdate.firstname,
        "lastname"-> playerToUpdate.lastname,
        "club"-> playerToUpdate.club,
        "rank"->playerToUpdate.rank.value,
        "seriesPlayerId"-> playerToUpdate.seriesPlayerId).executeUpdate()
  }

  def updateSeriesPlayerScores(playerToUpdate: SeriesPlayer) = DB.withConnection{ implicit conn =>
    SQL(
      """UPDATE seriesPlayers
        |SET wonMatches = {WM}, lostMatches = {LM}, wonSets = {WS}, lostSets = {LS}, wonPoints = {WP}, lostPoints = {LP}
        |WHERE seriesPlayerId = {seriesPlayerId}""".stripMargin).on(
        "WM"-> playerToUpdate.wonMatches,
        "LM"-> playerToUpdate.lostMatches,
        "WS"->playerToUpdate.wonSets,
        "LS" -> playerToUpdate.lostSets,
        "WP" -> playerToUpdate.wonPoints,
        "LP" -> playerToUpdate.lostPoints,
        "seriesPlayerId"-> playerToUpdate.seriesPlayerId).executeUpdate()
  }

  def deleteSeriesPlayer(seriesPlayerId: Long) = DB.withConnection{ implicit conn =>
    SQL("""DELETE FROM seriesPlayers WHERE seriesPlayerId = {seriesPlayerId}""")
      .on("seriesPlayerId" -> seriesPlayerId).executeUpdate()
  }

  def parseSeriesPlayerFromRow(row: Row): SeriesPlayer = SeriesPlayer(
      row[Long]("seriesPlayerId"),
    row[String]("firstname"),
    row[String]("lastname"),
    row[String]("club"),
    RankConverter.getRankOfInt(row[Int]("rank")),
    row[Int]("wonMatches"),
    row[Int]("lostMatches"),
    row[Int]("wonSets"),
    row[Int]("lostSets"),
    row[Int]("wonPoints"),
    row[Int]("lostPoints")
  )

  def getSeriesPlayer(seriesPlayerId: Long): Option[SeriesPlayer] = DB.withConnection{ implicit conn =>
    SQL("""SELECT * FROM seriesPlayers WHERE seriesPlayerId = {seriesPlayerId}""").on("seriesPlayerId"-> seriesPlayerId).map(row => parseSeriesPlayerFromRow(row)).singleOpt()
  }

  def getPlayersOfSeries(seriesId: Long): List[SeriesPlayer] = DB.withConnection{ implicit conn =>
    SQL("""SELECT * FROM seriesPlayers WHERE seriesId = {seriesId}""").on("seriesId" -> seriesId).list().map(parseSeriesPlayerFromRow)
  }
}
