package db

import controllers.{TournamentSeries, TournamentWrites}
import play.api.libs.json.Json
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.MongoDriver
import reactivemongo.api.commands.WriteResult

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class SeriesDb extends TournamentWrites{

  def getCount: Future[Int] = {
    val driver = new MongoDriver
    val connection = driver.connection(List("localhost:27017"))("pipoka_test")
    val db = connection.collection[JSONCollection]("players")
    val result = db.count()
    driver.close()
    result
  }

  def insertSeries(tournamentSeries: TournamentSeries): Future[WriteResult] = {
    val driver = new MongoDriver
    val connection = driver.connection(List("localhost:27017"))("pipoka_test")
    val db = connection.collection[JSONCollection]("players")
    val result = getCount.flatMap { count =>
      val nextId = (count + 1).toString
      val tournamentToInsert = tournamentSeries.copy(seriesId = nextId)
      db.insert(tournamentToInsert)
    }
    driver.close()
    result
  }

    /*DB.withConnection{ implicit conn =>
    Logger.info(tournamentSeries.toString)
    SQL(
      """INSERT INTO tournamentSeries(seriesName, seriesColor, numberOfSetsToWin, setTargetScore, playingWithHandicaps, extraHandicapForRecs, showReferees, tournamentId)
        |VALUES({seriesName}, {seriesColor},{numberOfSetsToWin}, {setTargetScore},{playingWithHandicaps},{extraHandicapForRecs},{showReferees},{tournamentId})
        |""".stripMargin).on(
    "seriesName" -> tournamentSeries.seriesName,
    "seriesColor" -> tournamentSeries.seriesColor,
    "numberOfSetsToWin" -> tournamentSeries.numberOfSetsToWin,
    "setTargetScore" -> tournamentSeries.setTargetScore,
    "playingWithHandicaps" -> tournamentSeries.playingWithHandicaps,
    "extraHandicapForRecs" -> tournamentSeries.extraHandicapForRecs,
    "showReferees" -> tournamentSeries.showReferees,
    "tournamentId" -> tournamentSeries.tournamentId
      ).executeInsert()

  }*/

  def updateSeries(tournamentSeries: TournamentSeries, seriesId: String): Future[WriteResult] = {
    val driver = new MongoDriver
    val connection = driver.connection(List("localhost:27017"))("pipoka_test")
    val db = connection.collection[JSONCollection]("players")
    val result = db.update(Json.obj("seriesId" -> seriesId), tournamentSeries)
    driver.close()
    result
  }

    /*DB.withConnection{ implicit conn =>
    Logger.info(tournamentSeries.toString)
    SQL(
      """UPDATE tournamentSeries
        |SET seriesName = {seriesName}, seriesColor = {seriesColor}, numberOfSetsToWin = {numberOfSetsToWin},
        | setTargetScore = {setTargetScore}, playingWithHandicaps = {playingWithHandicaps},
        | extraHandicapForRecs = {extraHandicapForRecs}, showReferees = {showReferees}
        |WHERE seriesId = {seriesId}
        """.stripMargin).on(
        "seriesName" -> tournamentSeries.seriesName,
        "seriesColor" -> tournamentSeries.seriesColor,
        "numberOfSetsToWin" -> tournamentSeries.numberOfSetsToWin,
        "setTargetScore" -> tournamentSeries.setTargetScore,
        "playingWithHandicaps" -> tournamentSeries.playingWithHandicaps,
        "extraHandicapForRecs" -> tournamentSeries.extraHandicapForRecs,
        "showReferees" -> tournamentSeries.showReferees,
        "seriesId" -> seriesId
      ).executeUpdate()
  }*/
  
  def deleteSeries(seriesId: String) = {
    val driver = new MongoDriver
    val connection = driver.connection(List("localhost:27017"))("pipoka_test")
    val db = connection.collection[JSONCollection]("series")
    val result = db.remove(Json.obj("seriesId"-> seriesId))
    driver.close()
    result
  }

  /*DB.withConnection { implicit conn =>
    SQL("""DELETE FROM tournamentSeries WHERE seriesId = {seriesId}""").on("seriesId" -> seriesId).executeUpdate()
  }*/

  def getSeries(seriesId: String): Future[Option[TournamentSeries]] = {
    val driver = new MongoDriver
    val connection = driver.connection(List("localhost:27017"))("pipoka_test")
    val db = connection.collection[JSONCollection]("series")
    val result = db
      .find(Json.obj("seriesId"-> seriesId))
      .cursor[TournamentSeries]()
      .collect[List](upTo = 1)
      .map(_.headOption)

    driver.close()
    result
  }
  
    /*DB.withConnection { implicit conn =>
      SQL("""SELECT * FROM tournamentSeries WHERE  seriesId = {seriesId}""").on("seriesId" -> seriesId).apply().headOption.map(parseSeriesFromRow)
  }*/

  def getSeriesListOfTournament(tournamentId: String) : Future[List[TournamentSeries]] = {

    val driver = new MongoDriver
    val connection = driver.connection(List("localhost:27017"))("pipoka_test")
    val db = connection.collection[JSONCollection]("series")
    val result = db
      .find(Json.obj("tournamentId" -> tournamentId))
      .cursor[TournamentSeries]()
      .collect[List]()
    driver.close()
    result
  }

    /*DB.withConnection{ implicit conn =>
      SQL("""SELECT * FROM tournamentSeries WHERE tournamentId = {tournamentId}""").on("tournamentId" -> tournamentId).list().map(parseSeriesFromRow)
  }*/
  /*
  def parseSeriesFromRow: (Row)=> TournamentSeries = { implicit row =>
    TournamentSeries(row[String]("seriesId"), row[String]("seriesName"), row[String]("seriesColor"), row[Int]("numberOfSetsToWin"),
      row[Int]("setTargetScore"), row[Boolean]("playingWithHandicaps"), row[Int]("extraHandicapForRecs"),
      row[Boolean]("showReferees"), row[Int]("tournamentId"), List(), List())
  }*/

}
