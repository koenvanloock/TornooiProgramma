package models

import utils.RankConverter

import scala.language.postfixOps
import slick.driver.MySQLDriver.api.{TableQuery => _, _}
import play.api.libs.json.{JsResult, JsValue, Json, JsObject}
import slick.jdbc.GetResult
import slick.lifted.{Tag, TableQuery}
import slick.model.ForeignKeyAction._
import slick.lifted.TableQuery
import play.api.libs.functional.syntax._

case class SeriesPlayer(seriesPlayerId: Option[String], playerId: String, rank: Rank, wonMatches: Int=0, lostMatches: Int=0, wonSets: Int=0, lostSets: Int=0, wonPoints: Int=0, lostPoints: Int=0, seriesId: String)

class SeriesPlayerTable(tag: Tag) extends Table[SeriesPlayer](tag, "SERIES_PLAYERS") {
  implicit def rankColumnType = MappedColumnType.base[Rank, Int](
    rank => rank.value,
    rankValue => RankConverter.getRankOfInt(rankValue)
  )
  def id = column[String]("SERIES_PLAYER_ID", O.PrimaryKey, O.Length(100))

  def playerId = column[String]("PLAYER_ID")

  def rank = column[Rank]("TOURNAMENT_RANK")

  def wonMatches = column[Int]("WON_MATCHES")
  def lostMatches = column[Int]("LOST_MATCHES")

  def wonSets = column[Int]("WON_SETS")
  def lostSets = column[Int]("LOST_SETS")

  def wonPoints = column[Int]("WON_POINTS")
  def lostPoints = column[Int]("LOST_POINTS")

  def seriesId = column[String]("SERIES_ID")


  def * = (id.?, playerId, rank, wonMatches, lostMatches, wonSets, lostSets, wonPoints, lostPoints, seriesId) <>((SeriesPlayer.apply _).tupled, SeriesPlayer.unapply)
}