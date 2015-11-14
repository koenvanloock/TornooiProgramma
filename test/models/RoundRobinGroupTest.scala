package models

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RoundRobinGroupTest extends Specification{
  "RoundRobin" should {
    "return complete = true when all it's matches are complete" in {
      val completedrobinGroup = RoundRobinGroup(1, 1,
        List(
          RobinPlayer(1,"Hans","Van Bael","pipoka", Ranks.E4, 0,0, 0,0,0,0),
          RobinPlayer(2,"Joris","Van Loock","pipoka", Ranks.Rec, 0,0, 0,0,0,0),
          RobinPlayer(3,"Gil","Corrujeira-Figueira","pipoka", Ranks.E2, 0,0, 0,0,0,0)),
        List(
          SiteMatch("1",List(SiteGame(21,15), SiteGame(21,17)),"1","2",4,isHandicapForB = true,21,2),
          SiteMatch("2",List(SiteGame(21,15), SiteGame(21,17)),"1","3",4,isHandicapForB = true,21,2),
            SiteMatch("3",List(SiteGame(21,15), SiteGame(21,17)),"2","3",4,isHandicapForB = true,21,2)
        ))

      completedrobinGroup.complete(2,21) must beTrue
    }

    "return complete = false when not all it's matches are complete" in {
      val completedrobinGroup = RoundRobinGroup(1, 1,
        List(
          RobinPlayer(1,"Hans","Van Bael","pipoka", Ranks.E4, 0,0, 0,0,0,0),
          RobinPlayer(2,"Joris","Van Loock","pipoka", Ranks.Rec, 0,0, 0,0,0,0),
          RobinPlayer(3,"Gil","Corrujeira-Figueira","pipoka", Ranks.E2, 0,0, 0,0,0,0)),
        List(
          SiteMatch("1",List(SiteGame(21,15), SiteGame(21,17)),"1","2",4,isHandicapForB = true,21,2),
          SiteMatch("2",List(SiteGame(21,15), SiteGame(21,17)),"1","3",4,isHandicapForB = true,21,2),
          SiteMatch("3",List(SiteGame(0,0), SiteGame(0,0)),"2","3",4,isHandicapForB = true,21,2)
        ))

      completedrobinGroup.complete(2,21) must beFalse
    }
  }
}
