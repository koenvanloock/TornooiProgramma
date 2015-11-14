package models

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SiteMatchTest extends Specification{
  "SiteMatch" should {
    "be complete with sets 21-5 and 21-16 for 2 winning to target 21" in {
      SiteMatch("1",List(SiteGame(21,5), SiteGame(21,16), SiteGame(0,0)), "0","1",5,isHandicapForB = true, 21, 2).isComplete must beTrue
    }
    "be complete with sets 21-5, 15-21 and 21-16 for 2 winning to target 21" in {
      SiteMatch("1",List(SiteGame(21,5),SiteGame(15,21), SiteGame(21,16)), "0","1",5,isHandicapForB = true, 21, 2).isComplete must beTrue
    }

    "be incomplete with sets 21-5 and 20-16 for 2 winning to target 21" in {
      SiteMatch("1",List(SiteGame(21,5), SiteGame(20,16), SiteGame(0,0)), "0","1",5,isHandicapForB = true, 21, 2).isComplete must beFalse
    }

    "be incomplete with sets 21-5 and 22-16 for 2 winning to target 21" in {
      SiteMatch("1",List(SiteGame(21,5), SiteGame(22,16), SiteGame(0,0)), "0","1",5,isHandicapForB = true, 21, 2).isComplete must beFalse
    }

    "be incomplete with sets 21-5, 20-16 and 21-14 for 2 winning to target 21" in {
      SiteMatch("1",List(SiteGame(21,5), SiteGame(21,16), SiteGame(21,14)), "0","1",5,isHandicapForB = true, 21, 2).isComplete must beFalse
    }

    "be complete with 5-11, 12-14 and 9-11" in {
      SiteMatch("1", List(SiteGame(5,11), SiteGame(12,14), SiteGame(9,11), SiteGame(0,0), SiteGame(0,0)), "0","1",0, isHandicapForB = true,11,3).isComplete must beTrue
    }

  }
}
