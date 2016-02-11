package db

import db.slick.{MatchDb, SeriesDb}
import helpers.TestConstants._
import models._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{WithApplication, PlaySpecification}

import scala.concurrent.Await

class MatchDbTest extends PlaySpecification{

  "MatchDb" should {
    def initMatchDb = {
      val appBuilder = new GuiceApplicationBuilder().build()
      val db = appBuilder.injector.instanceOf[MatchDb]
      Await.result(db.deleteAll, DEFAULT_DURATION)
      db
    }


    "Create a match with sets" in new WithApplication{
      val matchDb = initMatchDb
      val createdMatch = Await.result(matchDb.createDbMatch(SiteMatch(None,"1","2",5,isHandicapForB = true,21,2)), DEFAULT_DURATION)
      createdMatch must beEqualTo(SiteMatch(Some(createdMatch.matchId.get),"1","2",5,isHandicapForB = true,21,2))
    }

    "Create all matches for a robingroup" in new WithApplication{
      val matchDb = initMatchDb
      val playerList = List(
        RobinPlayer(None, None,"1", Ranks.B6.value,1,0,0,0,0,0,0),
        RobinPlayer(None, None,"4", Ranks.E6.value,2,0,0,0,0,0,0),
        RobinPlayer(None, None,"7", Ranks.Ng.value,3,0,0,0,0,0,0),
        RobinPlayer(None, None,"10", Ranks.C4.value,4,0,0,0,0,0,0))

      val result = Await.result(matchDb.createRobinMatches("1",playerList, 2, 21), DEFAULT_DURATION)

      result.length must beEqualTo(6)
      result.head.sets.length must beEqualTo(3)
    }

  }
}
