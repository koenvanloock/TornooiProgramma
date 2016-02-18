package models

import java.sql.Date
import java.time.LocalDate
import scala.language.postfixOps
import slick.driver.MySQLDriver.api.{TableQuery => _, _}
import play.api.libs.json.{JsResult, JsValue, Json, JsObject}
import slick.jdbc.GetResult
import slick.lifted.{Tag, TableQuery}
import slick.model.ForeignKeyAction._
import slick.lifted.TableQuery
import play.api.libs.functional.syntax._

case class Tournament(tournamentId: Option[String], tournamentName: String, tournamentDate: LocalDate, maximumNumberOfSeriesEntries: Int, hasMultipleSeries: Boolean, showClub: Boolean)

class TournamentTable(tag: Tag) extends Table[Tournament](tag, "TOURNAMENTS") {

  implicit val myDateColumnType = MappedColumnType.base[java.time.LocalDate, Date](
    ld => Date.valueOf(ld),
    d => d.toLocalDate
  )


  def id = column[String]("TOURNAMENT_ID", O.PrimaryKey, O.Length(100))
  def name = column[String]("TOURNAMENT_NAME")
  def date = column[LocalDate]("TOURNAMENT_DATE")
  def maxNumberOfSeriesEntries = column[Int]("MAX_NUMBER_OF_SERIESENTRIES")
  def hasMultipleSeries = column[Boolean]("HAS_MULTIPLE_SERIES")
  def showClub = column[Boolean]("SHOW_CLUB")

  def * = (id.?, name, date, maxNumberOfSeriesEntries, hasMultipleSeries, showClub) <>((Tournament.apply _).tupled, Tournament.unapply)
}