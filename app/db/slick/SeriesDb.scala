package db.slick


import com.google.inject.Inject
import models.{SeriesTable, AuthUser, TournamentSeries}
import org.slf4j.{LoggerFactory, Logger}
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.JdbcProfile
import slick.jdbc.GetResult
import utils.DbUtils

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

    val insertedId =  DbUtils.generateId
      db.run(SeriesCollection += seriesToInsert.copy(seriesId=Some(insertedId)))
        .map(_ => Some(seriesToInsert.copy(seriesId=Some(insertedId))))
    }

  def updateSeries(series: TournamentSeries): Future[Option[TournamentSeries]] = db.run(SeriesCollection.update(series)).map{ _ => (); Some(series) }

  def deleteSeries(seriesId: String): Future[Int] = db.run(SeriesCollection.filter(_.id === seriesId).delete)


  def getSeries(seriesId: String): Future[Option[TournamentSeries]] = db.run(SeriesCollection.filter(_.id === seriesId).result).map(_.headOption)

  def getSeriesListOfTournament(tournamentId: String): Future[List[TournamentSeries]] = {
    db.run(sql"""SELECT * FROM TOURNAMENT_SERIES WHERE TOURNAMENT_ID = $tournamentId""".as[TournamentSeries]).map(_.toList)
  }

  def deleteAll = db.run(SeriesCollection.delete)


}