package utils

import models.Ranks
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RankConverterTest extends Specification{

  "Rankconverter" should{
      "getRankOfInt with a number in the range should return a valid Rank" in {
        RankConverter.getRankOfInt(0) must beEqualTo(Ranks.Rec)
        RankConverter.getRankOfInt(1) must beEqualTo(Ranks.Ng)
        RankConverter.getRankOfInt(2) must beEqualTo(Ranks.F)
        RankConverter.getRankOfInt(3) must beEqualTo(Ranks.E6)
        RankConverter.getRankOfInt(4) must beEqualTo(Ranks.E4)
        RankConverter.getRankOfInt(5) must beEqualTo(Ranks.E2)
        RankConverter.getRankOfInt(6) must beEqualTo(Ranks.E0)
        RankConverter.getRankOfInt(7) must beEqualTo(Ranks.D6)
        RankConverter.getRankOfInt(8) must beEqualTo(Ranks.D4)
        RankConverter.getRankOfInt(9) must beEqualTo(Ranks.D2)
        RankConverter.getRankOfInt(10) must beEqualTo(Ranks.D0)
        RankConverter.getRankOfInt(11) must beEqualTo(Ranks.C6)
        RankConverter.getRankOfInt(12) must beEqualTo(Ranks.C4)
        RankConverter.getRankOfInt(13) must beEqualTo(Ranks.C2)
        RankConverter.getRankOfInt(14) must beEqualTo(Ranks.C0)
        RankConverter.getRankOfInt(15) must beEqualTo(Ranks.B6)
        RankConverter.getRankOfInt(16) must beEqualTo(Ranks.B4)
        RankConverter.getRankOfInt(17) must beEqualTo(Ranks.B2)
        RankConverter.getRankOfInt(18) must beEqualTo(Ranks.B0)
        RankConverter.getRankOfInt(19) must beEqualTo(Ranks.A)

      }

    "getRankOfInt with a number out of the range should return a Ranks.Rec" in {
      RankConverter.getRankOfInt(999) must beEqualTo(Ranks.Rec)
    }

  }
}
