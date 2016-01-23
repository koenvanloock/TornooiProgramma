package db

import db.slick.RobinDb
import models._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{WithApplication, PlaySpecification}
import helpers.TestConstants._
import utils.Draw

import scala.concurrent.Await

class RobinDbTest extends PlaySpecification{

  /*
  "RobinDb" should {

      def initRobinDb = {
        val appBuilder = new GuiceApplicationBuilder().build()
        val db = appBuilder.injector.instanceOf[RobinDb]
        //Await.result(db.deleteAll, DEFAULT_DURATION)
        db
      }


      NEEDS a valid RobinGroup in DB
    "insert a RobinPlayer" in new WithApplication{
      val robinPlayer=  RobinPlayer(None, Some(1), 1, 1,1,0,0,0,0,0,0)

      val player = Await.result(initRobinDb.createRobinPlayer(robinPlayer), DEFAULT_DURATION)
      player must beEqualTo(RobinPlayer(player.robinPlayerId, Some(1), 1, 1,1,0,0,0,0,0,0))
    }
  }*/
}
