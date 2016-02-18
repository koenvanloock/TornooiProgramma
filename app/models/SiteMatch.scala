package models

import play.api.libs.json._
import slick.jdbc.GetResult
import slick.lifted.TableQuery
import scala.language.postfixOps
import play.api.libs.functional.syntax._
import slick.driver.MySQLDriver.api.{TableQuery => _, _}

case class SiteMatchWithGames(matchId: Option[String],
                         playerA: String,
                         playerB: String,

                         handicap: Int,
                         isHandicapForB: Boolean,
                         targetScore: Int,
                         numberOfSetsToWin: Int,
                          sets: List[SiteGame])

object SiteMatchWithGames{
 implicit object SiteMatchWithGamesWriter {
   implicit val gameWrites = Json.writes[SiteGame]
   implicit val listGameWrites = new Writes[List[SiteGame]] {
     def writes(siteGameList: List[SiteGame]) = {
       Json.toJson(siteGameList.map(Json.toJson(_)))
     }
   }

   val owrites: OWrites[SiteMatchWithGames] = (
     (__ \ "matchId").writeNullable[String] and
       (__ \ "playerA").write[String] and
       (__ \ "playerB").write[String] and
       (__ \ "handicap").write[Int] and
       (__ \ "isHandicapForB").write[Boolean] and
       (__ \ "targetScore").write[Int] and
       (__ \ "numberOfSetsToWin").write[Int] and
       (__ \ "sets").write[List[SiteGame]](Writes.list[SiteGame] (gameWrites))
     ) (unlift(SiteMatchWithGames.unapply))

   implicit val matchWithGamesWrites = owrites.asInstanceOf[JsObject]
 }
}

case class SiteMatch(
                      matchId: Option[String],
                      playerA: String,
                      playerB: String,

                      handicap: Int,
                      isHandicapForB: Boolean,
                      targetScore: Int,
                      numberOfSetsToWin: Int) {

}

object SiteMatch extends Crudable[SiteMatch]{
  implicit object siteMatchTableModel extends TableModel[SiteMatchTable] {
    override def getRepId(tm: SiteMatchTable): Rep[String] = tm.id
  }
  override implicit val getResult: GetResult[SiteMatch] = GetResult(r => SiteMatch(r.<<, r.<<, r.<<,r.<<,r.<<,r.<<,r.<<))

  override def getId(m: SiteMatch): Option[String] = m.matchId

  override def setId(id: String)(m: SiteMatch): SiteMatch = m.copy(matchId = Some(id))

  val gameWrites = Json.writes[SiteGame]
  override def writes(o: SiteMatch): JsObject = Json.writes[SiteMatch].asInstanceOf[JsObject]

  val gameReads= Json.reads[SiteGame]
  implicit val matchReads = Json.reads[SiteMatch]
  override def reads(json: JsValue): JsResult[SiteMatch] = json.validate[SiteMatch]
}
class SiteMatchTable(tag: Tag) extends Table[SiteMatch](tag, "MATCHES") {

  def id = column[String]("MATCH_ID", O.PrimaryKey, O.Length(100))

  def playerA = column[String]("PLAYER_A")

  def playerB = column[String]("PLAYER_B")

  def handicap = column[Int]("HANDICAP")

  def isForB = column[Boolean]("IS_HANDICAP_FOR_B")

  def numberOfSetsToWin = column[Int]("NUMBER_OF_SETS_TO_WIN")

  def setTargetScore = column[Int]("SET_TARGET_SCORE")

  def * = (id.?, playerA, playerB, handicap, isForB, numberOfSetsToWin, setTargetScore) <>((SiteMatch.apply _).tupled, SiteMatch.unapply)
}

class RobinMatchTable(tag: Tag) extends Table[RobinMatch](tag, "ROBINMATCHES"){
  val siteMatches = TableQuery[SiteMatchTable]
  def id = column[String]("ROBIN_MATCH_ID", O.PrimaryKey, O.Length(100))
  def robinId = column[String]("ROBIN_ID")
  def matchId = column[String]("MATCH_ID")

  def siteMatch = foreignKey("FK_ROBINMATCH_MATCH", matchId , siteMatches)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
  //def robinRound= foreignKey("FK_ROBINMATCH_ROBINROUND", robinId ,)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

  def * = (id.?, robinId, matchId) <> (RobinMatch.tupled, RobinMatch.unapply)
}
