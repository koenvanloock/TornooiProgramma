package db.slick

import java.sql.Date
import java.time.LocalDate

import com.google.inject.Inject
import models._
import org.slf4j.{LoggerFactory, Logger}
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.JdbcProfile
import slick.jdbc.GetResult
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future



class TournamentDb @Inject()(@NamedDatabase("default") protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile]{
  import driver.api._

  //implicit val dateColumnType = MappedColumnType.base[Date, Long](d => d.getTime, d => new Date(d))

  implicit val myDateColumnType = MappedColumnType.base[java.time.LocalDate, Date](
    ld => Date.valueOf(ld),
    d => d.toLocalDate
  )

  private def TournamentCollection = TableQuery[TournamentTable]
  // create the db
  val schema = TournamentCollection.schema
  db.run(DBIO.seq(schema.create))

  implicit val getResult: GetResult[Tournament] = GetResult(r => Tournament(r.<<, r.<<, r.nextDate().toLocalDate, r.<<, r.<<, r.<<))


  private final val logger: Logger = LoggerFactory.getLogger(classOf[TournamentDb])

  def getNextId: Future[Int] = db.run(TournamentCollection.map(_.id).max.result).map( _.getOrElse(0) + 1)
  //db.run(sql"""SELECT max(tournamentId) FROM tournaments""".as[Int]).map(_.headOption)



  def insertTournament(tournamentToInsert: Tournament): Future[Option[Tournament]] = {

    getNextId.flatMap{id =>
      db.run(TournamentCollection += tournamentToInsert.copy(tournamentId = Some(id))).map{ _ => ()
        Some(tournamentToInsert.copy(tournamentId = Some(id)))
      }
    }
  }

  def updateTournament(tournamentToUpdate: Tournament): Future[Option[Tournament]] = {

    db.run(TournamentCollection.update(tournamentToUpdate)).map{ _ => (); Some(tournamentToUpdate) }
  }

  def deleteTournament(tournamentId: Int) = db.run(TournamentCollection.filter(_.id === tournamentId).delete)

  def getTournament(tournamentId: Int): Future[Option[Tournament]] = {
    db.run(sql"""SELECT * FROM TOURNAMENTS WHERE TOURNAMENT_ID = #$tournamentId""".as[Tournament]).map(_.headOption)
  }

  def getAllTournaments = db.run(sql"""SELECT * FROM TOURNAMENTS""".as[Tournament]).map(_.toList)

  def deleteAll = db.run(sql"""DELETE FROM TOURNAMENTS""".as[Int])

  private class TournamentTable(tag: Tag) extends Table[Tournament](tag, "TOURNAMENTS") {

    def id = column[Int]("TOURNAMENT_ID", O.PrimaryKey)
    def name = column[String]("TOURNAMENT_NAME")
    def date = column[LocalDate]("TOURNAMENT_DATE")
    def maxNumberOfSeriesEntries = column[Int]("MAX_NUMBER_OF_SERIESENTRIES")
    def hasMultipleSeries = column[Boolean]("HAS_MULTIPLE_SERIES")
    def showClub = column[Boolean]("SHOW_CLUB")

    def * = (id.?, name, date, maxNumberOfSeriesEntries, hasMultipleSeries, showClub) <>((Tournament.apply _).tupled, Tournament.unapply)
  }
}