package models

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RankTest extends Specification{
  "Ranks" should {
    "return the correct rank " in {
      Ranks.getRank("F") must beEqualTo(Ranks.F)
    }
  }
}
