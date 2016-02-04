package models

import play.api.libs.json._
import slick.jdbc.GetResult
import scala.language.postfixOps
import play.api.libs.functional.syntax._
import slick.driver.MySQLDriver.api.{TableQuery => _, _}

case class SiteMatchWithGames(matchId: Option[Int],
                         playerA: Int,
                         playerB: Int,

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
     (__ \ "matchId").writeNullable[Int] and
       (__ \ "playerA").write[Int] and
       (__ \ "playerB").write[Int] and
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
                      matchId: Option[Int],
                      playerA: Int,
                      playerB: Int,

                      handicap: Int,
                      isHandicapForB: Boolean,
                      targetScore: Int,
                      numberOfSetsToWin: Int) {

}

object SiteMatch extends Crudable[SiteMatch]{
  override implicit val getResult: GetResult[SiteMatch] = GetResult(r => SiteMatch(r.<<, r.<<, r.<<,r.<<,r.<<,r.<<,r.<<))

  override def getId(m: SiteMatch): Option[Int] = m.matchId

  override def setId(id: Int)(m: SiteMatch): SiteMatch = m.copy(matchId = Some(id))

  val gameWrites = Json.writes[SiteGame]
  override def writes(o: SiteMatch): JsObject = Json.writes[SiteMatch].asInstanceOf[JsObject]

  val gameReads= Json.reads[SiteGame]
  implicit val matchReads = Json.reads[SiteMatch]
  override def reads(json: JsValue): JsResult[SiteMatch] = json.validate[SiteMatch]
}

case class SiteGame(id: Option[Int], matchId: Int, pointA: Int, pointB : Int){
  def isCorrect(targetScore: Int): Boolean ={
    if(pointA==targetScore){
      pointA - pointB > 1
    }else if (pointB == targetScore){
      pointB - pointA  > 1
    }else{
      if(pointA > targetScore && pointA> pointB){
        pointA - pointB == 2
      }else{
        pointB - pointA == 2
      }
    }
  }
}


object SiteGame extends Crudable[SiteGame]{
  override implicit val getResult: GetResult[SiteGame] = GetResult(r => SiteGame(r.<<, r.<<, r.<<, r.<<))

  override def getId(m: SiteGame): Option[Int] = m.id

  override def setId(id: Int)(m: SiteGame): SiteGame = m.copy(id = Some(id))

  override def writes(o: SiteGame): JsObject = Json.writes[SiteGame].asInstanceOf[JsObject]

  implicit val gameReads= Json.reads[SiteGame]
  override def reads(json: JsValue): JsResult[SiteGame] = json.validate[SiteGame]
}

