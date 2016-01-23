package models

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RoundRobinGroupTest extends Specification {
  "RoundRobin" should {
    "return complete = true when all it's matches are complete" in {
      val completedrobinGroup = RoundRobinGroup(Some(1), 1,
        List(
          RobinPlayer(Some(1), Some(1), 1, Ranks.E0.value, 1, 0, 0, 0, 0, 0, 0),
          RobinPlayer(Some(2), Some(1), 2, Ranks.E0.value, 2, 0, 0, 0, 0, 0, 0),
          RobinPlayer(Some(3), Some(1), 3, Ranks.E0.value, 3, 0, 0, 0, 0, 0, 0)),
        List(
          SiteMatch(Some(1), List(SiteGame(21, 15), SiteGame(21, 17)), 1, 2, 4, isHandicapForB = true, 21, 2),
          SiteMatch(Some(2), List(SiteGame(21, 15), SiteGame(21, 17)), 1, 3, 4, isHandicapForB = true, 21, 2),
          SiteMatch(Some(3), List(SiteGame(21, 15), SiteGame(21, 17)), 2, 3, 4, isHandicapForB = true, 21, 2)
        ))

      completedrobinGroup.complete(2, 21) must beTrue
    }

    "return complete = false when not all it's matches are complete" in {
      val completedrobinGroup = RoundRobinGroup(Some(1), 1,
        List(
          RobinPlayer(Some(1), Some(1), 1, Ranks.E0.value,  1, 0, 0, 0, 0, 0, 0),
          RobinPlayer(Some(2), Some(1), 2, Ranks.E0.value, 2, 0, 0, 0, 0, 0, 0),
          RobinPlayer(Some(3), Some(1), 3, Ranks.E0.value, 3, 0, 0, 0, 0, 0, 0)
      ),
      List(
        SiteMatch(Some(1), List(SiteGame(21, 15), SiteGame(21, 17)), 1, 2, 4, isHandicapForB = true, 21, 2),
        SiteMatch(Some(2), List(SiteGame(21, 15), SiteGame(21, 17)), 1, 3, 4, isHandicapForB = true, 21, 2),
        SiteMatch(Some(3), List(SiteGame(0, 0), SiteGame(0, 0)), 2, 3, 4, isHandicapForB = true, 21, 2)
      ))

      completedrobinGroup.complete(2, 21) must beFalse
    }
  }
}
