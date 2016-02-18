package models


import scala.language.postfixOps
import slick.driver.MySQLDriver.api.{TableQuery => _, _}
import play.api.libs.json.{JsResult, JsValue, Json, JsObject}
import slick.jdbc.GetResult
import slick.lifted.{Tag, TableQuery}
import slick.model.ForeignKeyAction._
import slick.lifted.TableQuery
import play.api.libs.functional.syntax._
import slick.driver.MySQLDriver.api.{TableQuery => _, _}

case class RobinPlayer(robinPlayerId: Option[String], robinGroupId: Option[String], seriesPlayerId: String, rankValue: Int, robinNr: Int, wonMatches: Int, lostMatches: Int, wonSets: Int, lostSets: Int, wonPoints: Int, lostPoints: Int)

class RobinPlayerTable(tag: Tag) extends Table[RobinPlayer](tag, "ROBIN_PLAYERS") {

  val roundRobinCollection = TableQuery[RoundRobinTable]
  def id = column[String]("ROBIN_PLAYER_ID", O.PrimaryKey, O.Length(100))
  def roundRobinGroupId = column[String]("ROBIN_GROUP_ID")

  def seriesPlayerId = column[String]("SERIES_PLAYER_ID")
  def rankValue = column[Int]("RANK_VALUE")
  def robinNr = column[Int]("ROBIN_NR")

  def wonMatches = column[Int]("WON_MATCHES")
  def lostMatches = column[Int]("LOST_MATCHES")
  def wonSets = column[Int]("WON_SETS")
  def lostSets = column[Int]("LOST_SETS")
  def wonPoints = column[Int]("WON_POINTS")
  def lostPoints = column[Int]("LOST_POINTS")

  def robinRoundGroup = foreignKey("FK_ROBINPLAYER_ROBIN_GROUP", roundRobinGroupId ,roundRobinCollection)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

  def * = (id.?, roundRobinGroupId.?, seriesPlayerId, rankValue, robinNr, wonMatches, lostMatches, wonSets, lostSets, wonPoints, lostPoints) <> (RobinPlayer.tupled, RobinPlayer.unapply)
}