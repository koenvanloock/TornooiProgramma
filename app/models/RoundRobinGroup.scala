package models

import scala.language.postfixOps
import slick.driver.MySQLDriver.api.{TableQuery => _, _}
import play.api.libs.json.{JsResult, JsValue, Json, JsObject}
import slick.jdbc.GetResult
import slick.lifted.{Tag, TableQuery}
import slick.model.ForeignKeyAction._
import slick.lifted.TableQuery
import play.api.libs.functional.syntax._


case class RoundRobinGroup(robinId: Option[String], seriesRoundId: String){//, robinPlayers: List[RobinPlayer], robinMatches: List[SiteMatch]){


  //def complete(numberOfSetsToWin: Int, setTargetScore: Int): Boolean = robinMatches.forall( _.isComplete)
}


class RoundRobinTable(tag: Tag) extends Table[RoundRobinGroup](tag, "ROBIN_ROUNDS") {

  def id = column[String]("ROUND_ROBIN_ID", O.PrimaryKey, O.Length(100))
  def seriesRoundId = column[String]("SERIESROUND_ID")

  def * = (id.?, seriesRoundId) <> ((RoundRobinGroup.apply _ ).tupled, RoundRobinGroup.unapply)
}