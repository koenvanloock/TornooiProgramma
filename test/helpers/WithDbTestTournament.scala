package helpers

import java.time.LocalDate

import anorm._
import controllers.TournamentWrites
import models.Tournament
import org.specs2.execute.{Result, AsResult}
import play.api.db.DB
import play.api.libs.json._
import play.modules.reactivemongo.json._
import play.api.test.WithApplication
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import utils.DbUtils

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global


abstract class WithDbTestTournament extends WithApplication with TournamentWrites{
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

    val collection = db.collection[JSONCollection]("tournaments")
    Await.result(
      collection.remove(Json.obj()),
      Duration(3000, "millis"))
    Await.result(collection.insert(Tournament(Some("1"),"Clubkampioenschap", LocalDate.of(2015,5,12), 0, false, false)), Duration(3000, "millis"))
    /* DB.withConnection{
       implicit conn =>
         SQL("TRUNCATE tournaments").execute()
         SQL("INSERT INTO tournaments(tournamentName,tournamentDate, maxSeriesEntries,hasMultipleSeries,showClub) VALUES('Clubkampioenschap','2015-05-12',0,false,false)").execute()
     }*/
  }


  def tearDown(driver: MongoDriver, connection: MongoConnection) = {
    driver.close()
    connection.close()
  }
}
