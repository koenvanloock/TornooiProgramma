package models

import scala.language.postfixOps
import slick.driver.MySQLDriver.api.{TableQuery => _, _}
import play.api.libs.json.{JsResult, JsValue, Json, JsObject}
import slick.jdbc.GetResult
import slick.lifted.{Tag, TableQuery}
import slick.model.ForeignKeyAction._
import slick.lifted.TableQuery
import play.api.libs.functional.syntax._

sealed trait SeriesRound

case class SiteBracketRound(seriesRoundId: Option[String], numberOfBracketRounds: Int, roundType: String, seriesId: String, roundNr: Int) extends SeriesRound
case class RobinRound(seriesRoundId: Option[String], numberOfRobins: Int, roundType: String,seriesId: String, roundNr: Int) extends SeriesRound

case class GenericSeriesRound(seriesRoundId: Option[String], numberOfBracketRounds: Option[Int], numberOfRobins: Option[Int], roundType: String, seriesId: String, roundNr: Int)

class GenericSeriesRoundTable(tag: Tag) extends Table[GenericSeriesRound](tag, "SERIES_ROUNDS") {

  def id = column[String]("SERIES_ROUND_ID", O.PrimaryKey, O.Length(100))

  def numberOfBrackets = column[Option[Int]]("NUMBER_OF_BRACKETS")

  def numberOfRobins = column[Option[Int]]("NUMBER_OF_ROBINS")

  def roundType = column[String]("ROUND_TYPE")

  def seriesId = column[String]("SERIES_ID")

  def roundNr = column[Int]("ROUND_NR")


  def * = (id.?, numberOfBrackets, numberOfRobins, roundType, seriesId, roundNr) <>((GenericSeriesRound.apply _).tupled, GenericSeriesRound.unapply)
}