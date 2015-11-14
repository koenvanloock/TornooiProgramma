package helpers

import controllers.Player
import models.{Ranks, Rank}
import org.specs2.execute.{Result, AsResult}
import play.modules.reactivemongo.json._
import play.api.libs.json.{JsObject, Json}
import play.api.test.WithApplication
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.{MongoConnection, DefaultDB, MongoDriver}
import utils.DbUtils
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration


abstract class WithPlayerDbSetup extends WithApplication {


  override def around[T: AsResult](t: => T): Result = super.around {
    val driver = new MongoDriver
    val connection = driver.connection(List("localhost"))
    val db = connection("pipoka_test")
    setupData(db)
    val testResult = t
    tearDown(driver, connection)

    testResult
  }

  def setupData(db: DefaultDB) = {

        implicit val rankWrites = Json.format[Rank]
        implicit val playerWithIdWrites = Json.format[Player]
        val testPlayer = Player(Some("1"),"Koen","Van Loock",Ranks.D2)
        val collection = db.collection[JSONCollection]("players")
        Await.result(collection.remove(Json.obj()), Duration(3000, "millis"))
        Await.result(collection.insert(testPlayer), Duration(3000, "millis"))
  }


  def tearDown(driver: MongoDriver, connection: MongoConnection) = {
    driver.close()
    connection.close()
  }
}