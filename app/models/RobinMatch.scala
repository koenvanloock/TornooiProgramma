package models

import play.api.libs.json.{JsResult, JsValue, JsObject, Json}
import slick.driver.MySQLDriver.api.{TableQuery => _, _}
import slick.jdbc.GetResult
import slick.lifted.Tag

import scala.language.postfixOps

case class RobinMatch(robinMatchId: Option[String], robinId: String, matchId: String)

object RobinMatch extends Crudable[RobinMatch]{
  implicit object robinMatchTableModel extends TableModel[RobinMatchTable] {
    override def getRepId(tm: RobinMatchTable): Rep[String] = tm.id
  }
  override implicit val getResult: GetResult[RobinMatch] = GetResult(r => RobinMatch(r.<<, r.<<, r.<<))

  override def getId(m: RobinMatch): Option[String] = m.robinMatchId

  override def setId(id: String)(m: RobinMatch): RobinMatch = m.copy(robinMatchId = Some(id))

  val gameWrites = Json.writes[SiteGame]
  override def writes(o: RobinMatch): JsObject = Json.writes[RobinMatch].asInstanceOf[JsObject]

  val gameReads= Json.reads[SiteGame]
  implicit val matchReads = Json.reads[RobinMatch]
  override def reads(json: JsValue): JsResult[RobinMatch] = json.validate[RobinMatch]
}
