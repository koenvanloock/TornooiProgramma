package models

import utils.RankConverter

import scala.language.postfixOps
import slick.driver.MySQLDriver.api.{TableQuery => _, _}
import play.api.libs.json._
import slick.jdbc.GetResult
import slick.lifted.{Tag, TableQuery}
import slick.model.ForeignKeyAction._
import slick.lifted.TableQuery
import play.api.libs.functional.syntax._
import slick.driver.MySQLDriver.api.{TableQuery => _, _}

case class Player(playerId: Option[String], firstname: String, lastname: String, rank: Rank)
object Player extends Crudable[Player] with OFormat[Player]{
  implicit val rankFormat = Json.format[Rank]
  //implicit val playerFormat = Json.format[Player]
  override implicit val getResult: GetResult[Player] = GetResult(r => Player(r.<<, r.<<, r.<<, utils.RankConverter.getRankOfInt(r.nextInt())))

  override def getId(m: Player): Option[String] = m.playerId

  override def setId(id: String)(m: Player): Player = m.copy(playerId = Some(id))

  implicit val jsonWrits = Json.writes[Player]
  override def writes(o: Player): JsObject = Json.writes[Player].asInstanceOf[JsObject]

  implicit val readPlayer = Json.reads[Player]
  override def reads(json: JsValue): JsResult[Player] = json.validate[Player]
}

class PlayerTable(tag: Tag) extends Table[Player](tag, "PLAYERS") {
  implicit def rankColumnType = MappedColumnType.base[Rank, Int](
    rank => rank.value,
    rankValue => RankConverter.getRankOfInt(rankValue)
  )
  def id = column[String]("PLAYER_ID", O.PrimaryKey, O.Length(100))
  def firstname = column[String]("FIRSTNAME")
  def lastname = column[String]("LASTNAME")
  def rank = column[Rank]("RANK")

  def * = (id.?, firstname, lastname, rank) <> ((Player.apply _ ).tupled, Player.unapply)
}