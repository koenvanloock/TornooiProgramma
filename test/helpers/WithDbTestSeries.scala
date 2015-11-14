package helpers

import controllers.{TournamentWrites, TournamentSeries}
import org.specs2.execute.{Result, AsResult}
import play.modules.reactivemongo.json._
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.Json
import play.api.test.WithApplication
import play.modules.reactivemongo.json.collection.JSONCollection
import utils.DbUtils

import scala.concurrent.Await
import scala.concurrent.duration.Duration

abstract class WithDbTestSeries extends WithApplication with TournamentWrites{
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
    def collection = db.collection[JSONCollection]("series")
    Await.result(collection.remove(Json.obj()), Duration(3000, "millis"))
    Await.result(
      collection.insert(TournamentSeries("1","Open zonder voorgift","#ffffff",2, 21,playingWithHandicaps = true, 0,showReferees = true,1, List(), List())),
      Duration(3000, "millis"))
    /*DB.withConnection{
      implicit conn =>
        SQL("TRUNCATE tournamentSeries").execute()
        SQL("""INSERT INTO tournamentSeries VALUES(1,"Open zonder voorgift", "#ffffff",2,21,true,0,true,1)""").execute()
    }*/
  }


  def tearDown(driver: MongoDriver, connection: MongoConnection) = {
    driver.close()
    connection.close()
  }
}

