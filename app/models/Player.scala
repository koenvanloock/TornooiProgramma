package models

import slick.jdbc.GetResult
import play.api.libs.json.Reads._
import play.api.libs.json._

case class Player(playerId: Option[Int], firstname: String, lastname: String, rank: Rank)
object Player extends Crudable[Player] with OFormat[Player]{
  implicit val rankFormat = Json.format[Rank]
  //implicit val playerFormat = Json.format[Player]
  override implicit val getResult: GetResult[Player] = GetResult(r => Player(r.<<, r.<<, r.<<, utils.RankConverter.getRankOfInt(r.nextInt())))

  override def getId(m: Player): Option[Int] = m.playerId

  override def setId(id: Int)(m: Player): Player = m.copy(playerId = Some(id))

  implicit val jsonWrits = Json.writes[Player]
  override def writes(o: Player): JsObject = Json.writes[Player].asInstanceOf[JsObject]

  implicit val readPlayer = Json.reads[Player]
  override def reads(json: JsValue): JsResult[Player] = json.validate[Player]
}