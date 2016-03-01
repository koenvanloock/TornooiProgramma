package models

import scala.language.postfixOps
import slick.driver.MySQLDriver.api.{TableQuery => _, _}
import play.api.libs.json._
import slick.jdbc.GetResult
import slick.lifted.{Tag, TableQuery}
import slick.model.ForeignKeyAction._
import slick.lifted.TableQuery
import play.api.libs.functional.syntax._


case class RobinGroupWithMatchesAndPlayers(robinId: String, seriesRoundId: String, robinPlayers: List[RobinPlayerWithName], robinMatches: List[SiteMatchWithGames])

object RobinGroupFormat{
  implicit val gameWrites = Json.writes[SiteGame]
  implicit val robinMatchWrites = new Writes[SiteMatchWithGames] {
    def writes(pingpongMatch: SiteMatchWithGames) = Json.obj(
      "playerANr" -> Json.toJson(pingpongMatch.playerA),
      "playerBNr" -> Json.toJson( pingpongMatch.playerB),
      "handicap" -> Json.toJson( pingpongMatch.handicap),
      "isHandicapForB" -> Json.toJson( pingpongMatch.isHandicapForB),
      "sets" -> Json.toJson(pingpongMatch.sets.map(Json.toJson(_)))
    )
  }
  implicit val robinPlayerWrites = Json.writes[RobinPlayerWithName]
  implicit val robinGroupWithMatchesAndPlayersWrites = new Writes[RobinGroupWithMatchesAndPlayers] {
    def writes(robinRound: RobinGroupWithMatchesAndPlayers) = Json.obj(
      "robinId" -> Json.toJson(robinRound.robinId),
    "seriesId" -> Json.toJson(robinRound.seriesRoundId),
    "robinPlayers" -> Json.toJson(robinRound.robinPlayers.map(player => Json.toJson(player))),
    "robinMatches" -> Json.toJson(robinRound.robinMatches.map(robinMatch => Json.toJson(robinMatch)))

    )
  }
}

case class RoundRobinGroup(robinId: Option[String], seriesRoundId: String){//, robinPlayers: List[RobinPlayer], robinMatches: List[SiteMatch]){

  //def complete(numberOfSetsToWin: Int, setTargetScore: Int): Boolean = robinMatches.forall( _.isComplete)
}


class RoundRobinTable(tag: Tag) extends Table[RoundRobinGroup](tag, "ROBIN_ROUNDS") {

  def id = column[String]("ROUND_ROBIN_ID", O.PrimaryKey, O.Length(100))
  def seriesRoundId = column[String]("SERIESROUND_ID")

  def * = (id.?, seriesRoundId) <> ((RoundRobinGroup.apply _ ).tupled, RoundRobinGroup.unapply)
}