package models

import play.api.libs.json._
import slick.driver.MySQLDriver.api.{TableQuery => _, _}
import slick.jdbc.GetResult
import slick.lifted.{TableQuery, Tag}

import scala.language.postfixOps

case class BracketMatch(bracketMatchId: Option[String], bracketRoundId: String, matchId: String)

object BracketMatch{

  implicit object CrudableBracketMatch extends Crudable[BracketMatch] with OFormat[BracketMatch] {

    implicit object BracketMatchTableModel extends TableModel[BracketMatchTable] {
      override def getRepId(tm: BracketMatchTable): Rep[String] = tm.id
    }

    override implicit val getResult: GetResult[BracketMatch] = GetResult(r => BracketMatch(r.<<, r.<<, r.<<))

    override def getId(m: BracketMatch): Option[String] = m.bracketMatchId

    override def setId(id: String)(m: BracketMatch): BracketMatch = m.copy(bracketMatchId = Some(id))

    val gameWrites = Json.writes[SiteGame]

    override def writes(o: BracketMatch): JsObject = Json.writes[BracketMatch].asInstanceOf[JsObject]

    val gameReads = Json.reads[SiteGame]
    implicit val matchReads = Json.reads[BracketMatch]

    override def reads(json: JsValue): JsResult[BracketMatch] = json.validate[BracketMatch]
  }
}

class BracketMatchTable(tag: Tag) extends Table[BracketMatch](tag, "BRACKET_MATCHES"){
  val siteMatches = TableQuery[SiteMatchTable]
  def id = column[String]("BRACKET_MATCH_ID", O.PrimaryKey, O.Length(100))
  def bracketRoundId = column[String]("BRACKET_ROUND_ID")
  def matchId = column[String]("MATCH_ID")

  def * = (id.?, bracketRoundId, matchId) <> ((BracketMatch.apply _).tupled, BracketMatch.unapply)
}

