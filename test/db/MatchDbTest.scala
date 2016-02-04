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
      //Await.result(db.deleteAll, DEFAULT_DURATION)
      db
    }


    "Create a match with sets" in new WithApplication{
      val matchDb = initMatchDb
      val createdMatch = Await.result(matchDb.createDbMatch(SiteMatch(None,1,2,5,isHandicapForB = true,21,2)), DEFAULT_DURATION)
      createdMatch must beEqualTo(SiteMatch(Some(createdMatch.matchId.get),1,2,5,isHandicapForB = true,21,2))
    }

    "Create all matches for a robingroup" in new WithApplication{
      val matchDb = initMatchDb
      val idStart = Await.result(matchDb.getNextMatchId, DEFAULT_DURATION)
      val playerList = List(
        RobinPlayer(None, None,1, Ranks.B6.value,1,0,0,0,0,0,0),
        RobinPlayer(None, None,4, Ranks.E6.value,2,0,0,0,0,0,0),
        RobinPlayer(None, None,7, Ranks.Ng.value,3,0,0,0,0,0,0),
        RobinPlayer(None, None,10, Ranks.C4.value,4,0,0,0,0,0,0))

      Await.result(matchDb.createRobinMatches(1,playerList, 2, 21), DEFAULT_DURATION) must beEqualTo(
        List(SiteMatchWithGames(Some(6),4,10,9,false,21,2,List(SiteGame(Some(1),0,0,0), SiteGame(Some(2),0,0,0), SiteGame(Some(3),0,0,0))),
           SiteMatchWithGames(Some(5),4,7,2,true,21,2,List(SiteGame(Some(4),0,0,0), SiteGame(Some(5),0,0,0), SiteGame(Some(6),0,0,0))),
           SiteMatchWithGames(Some(4),1,10,3,true,21,2,List(SiteGame(Some(7),0,0,0), SiteGame(Some(8),0,0,0), SiteGame(Some(9),0,0,0))),
           SiteMatchWithGames(Some(3),1,7,14,true,21,2,List(SiteGame(Some(10),0,0,0), SiteGame(Some(11),0,0,0), SiteGame(Some(12),0,0,0))),
           SiteMatchWithGames(Some(7),7,10,11,false,21,2,List(SiteGame(Some(13),0,0,0), SiteGame(Some(14),0,0,0), SiteGame(Some(15),0,0,0))),
           SiteMatchWithGames(Some(2),1,4,12,true,21,2,List(SiteGame(Some(16),0,0,0), SiteGame(Some(17),0,0,0), SiteGame(Some(18),0,0,0)))))

    }

  }
}
