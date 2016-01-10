package db.slick


import com.google.inject.Inject
import models.{AuthUser, TournamentSeries}
import org.slf4j.{LoggerFactory, Logger}
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.JdbcProfile
import slick.jdbc.GetResult

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SeriesDb @Inject()(@NamedDatabase("default") protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile]{

  implicit val getResult: GetResult[TournamentSeries] = GetResult(r => TournamentSeries(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))
  import driver.api._

  private final val logger: Logger = LoggerFactory.getLogger(classOf[SeriesDb])
  // create the db
  val schema = SeriesCollection.schema
  db.run(DBIO.seq(schema.create))

  private def SeriesCollection = TableQuery[SeriesTable]

  def insertSeries(seriesToInsert: TournamentSeries): Future[Option[TournamentSeries]] = {

      db.run((SeriesCollection returning SeriesCollection.map(_.id)
        into ((series,id) => Some(series.copy(seriesId=Some(id))))
        ) += seriesToInsert)
    }

  def updateSeries(series: TournamentSeries): Future[Option[TournamentSeries]] = db.run(SeriesCollection.update(series)).map{ _ => (); Some(series) }

  def deleteSeries(seriesId: Int): Future[Int] = db.run(SeriesCollection.filter(_.id === seriesId).delete)


  def getSeries(seriesId: Int): Future[Option[TournamentSeries]] = db.run(SeriesCollection.filter(_.id === seriesId).result).map(_.headOption)

  def getSeriesListOfTournament(tournamentId: Int): Future[List[TournamentSeries]] = {
    db.run(sql"""SELECT * FROM TOURNAMENT_SERIES WHERE TOURNAMENT_ID = #$tournamentId""".as[TournamentSeries]).map(_.toList)
  }

  def deleteAll = db.run(SeriesCollection.delete)

  private class SeriesTable(tag: Tag) extends Table[TournamentSeries](tag, "TOURNAMENT_SERIES") {

    def id = column[Int]("SERIES_ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("SERIES_NAME")
    def seriesColor = column[String]("PASSWORD")
    def numberOfSetsToWin = column[Int]("NUMBER_OF_SETS_TO_WIN")
    def setTargetScore = column[Int]("SET_TARGET_SCORE")
    def playingWithHandicaps = column[Boolean]("PLAYING_WITH_HANDICAPS")
    def extraHandicapForRecs = column[Int]("EXTRA_HANDICAP_FOR_RECS")
    def showReferees= column[Boolean]("SHOW_REFEREES")
    def tournamentId = column[Int]("TOURNAMENT_ID")

    def * = (id.?, name, seriesColor,numberOfSetsToWin, setTargetScore, playingWithHandicaps, extraHandicapForRecs, showReferees, tournamentId) <> ((TournamentSeries.apply _ ).tupled, TournamentSeries.unapply)
  }
}