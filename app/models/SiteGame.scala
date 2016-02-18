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


case class SiteGame(id: Option[String], matchId: String, pointA: Int, pointB : Int){
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

  override def getId(m: SiteGame): Option[String] = m.id

  override def setId(id: String)(m: SiteGame): SiteGame = m.copy(id = Some(id))

  override def writes(o: SiteGame): JsObject = Json.writes[SiteGame].asInstanceOf[JsObject]

  implicit val gameReads= Json.reads[SiteGame]
  override def reads(json: JsValue): JsResult[SiteGame] = json.validate[SiteGame]
}


class SiteGameTable(tag: Tag) extends Table[SiteGame](tag, "SETSTABLE") {
  val siteMatches =  TableQuery[SiteMatchTable]
  def id = column[String]("SET_ID", O.PrimaryKey, O.Length(100))
  def matchId = column[String]("MATCH_ID")
  def pointA = column[Int]("POINT_A")

  def pointB = column[Int]("POINT_B")
  def siteMatch = foreignKey("FK_GAME_MATCH", matchId , siteMatches)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)


  def * = (id.?, matchId, pointA, pointB) <>((SiteGame.apply _).tupled, SiteGame.unapply)
}
