package db

import db.slick.{MatchDb, SeriesDb}
import helpers.TestConstants._
import models.{Ranks, RobinPlayer, SiteGame, SiteMatch}
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
      val createdMatch = Await.result(matchDb.createDbMatch(SiteMatch(None,List(SiteGame(0,0),SiteGame(0,0),SiteGame(0,0)),1,2,5,isHandicapForB = true,21,2)), DEFAULT_DURATION)
      createdMatch must beEqualTo(SiteMatch(Some(createdMatch.matchId.get),List(SiteGame(0,0),SiteGame(0,0),SiteGame(0,0)),1,2,5,isHandicapForB = true,21,2))
    }

    "Create all matches for a robingroup" in new WithApplication{
      val matchDb = initMatchDb
      val idStart = Await.result(matchDb.getNextMatchId, DEFAULT_DURATION)
      val playerList = List(
        RobinPlayer(None, None,1, Ranks.B6.value,1,0,0,0,0,0,0),
        RobinPlayer(None, None,4, Ranks.E6.value,2,0,0,0,0,0,0),
        RobinPlayer(None, None,7, Ranks.Ng.value,3,0,0,0,0,0,0),
        RobinPlayer(None, None,10, Ranks.C4.value,4,0,0,0,0,0,0))

      Await.result(matchDb.createRobinMatches(playerList, 2, 21), DEFAULT_DURATION) must beEqualTo(
        List(SiteMatch(Some(idStart),List(SiteGame(0,0), SiteGame(0,0), SiteGame(0,0)),1,4,12,true,21,2),
          SiteMatch(Some(idStart +1),List(SiteGame(0,0), SiteGame(0,0), SiteGame(0,0)),1,7,14,true,21,2),
          SiteMatch(Some(idStart +2),List(SiteGame(0,0), SiteGame(0,0), SiteGame(0,0)),1,10,3,true,21,2),
          SiteMatch(Some(idStart +3),List(SiteGame(0,0), SiteGame(0,0), SiteGame(0,0)),4,7,2,true,21,2),
          SiteMatch(Some(idStart +4),List(SiteGame(0,0), SiteGame(0,0), SiteGame(0,0)),4,10,9,false,21,2),
          SiteMatch(Some(idStart +5),List(SiteGame(0,0), SiteGame(0,0), SiteGame(0,0)),7,10,11,false,21,2)))
    }

  }
}
