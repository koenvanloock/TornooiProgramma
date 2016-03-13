package models

import play.api.libs.json.{Json, JsResult, JsValue, JsObject}
import slick.jdbc.GetResult
import slick.lifted.Tag
import utils.RankConverter
import slick.driver.MySQLDriver.api.{TableQuery => _, _}


case class BracketRound(id: Option[String], siteBracketRoundId: String, roundNr: Int)

object BracketRound {
  implicit object crudableBracketRound extends Crudable[BracketRound]{
    override implicit val getResult: GetResult[BracketRound] = GetResult(r => BracketRound(r.<<, r.<<, r.<<))

    override def getId(m: BracketRound): Option[String] = m.id

    override def setId(id: String)(m: BracketRound): BracketRound = m.copy(id= Some(id))

    override def reads(json: JsValue): JsResult[BracketRound] = json.validate[BracketRound]

    override def writes(o: BracketRound): JsObject = Json.writes[BracketRound].asInstanceOf[JsObject]
  }


  class BracketRoundTable(tag: Tag) extends Table[BracketRound](tag, "BRACKET_ROUNDS") {
    implicit def rankColumnType = MappedColumnType.base[Rank, Int](
      rank => rank.value,
      rankValue => RankConverter.getRankOfInt(rankValue)
    )
    def id = column[String]("PLAYER_ID", O.PrimaryKey, O.Length(100))
    def siteBracketRoundId = column[String]("SITE_BRACKET_ID")
    def roundNr = column[Int]("ROUND_NR")

    def * = (id.?, siteBracketRoundId, roundNr) <> ((BracketRound.apply _ ).tupled, BracketRound.unapply)
  }
}
