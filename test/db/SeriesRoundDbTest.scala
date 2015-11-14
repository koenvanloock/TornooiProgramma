package db

import controllers.SiteBracketRound
import helpers.WithDbTestSeries
import org.specs2.mutable.Specification

class SeriesRoundDbTest extends Specification{
  "SeriesRoundDb" should {

      "insert a new bracketRound" in new WithDbTestSeries {
        val bracketToInsert = SiteBracketRound(Some(1),3,"B",1)
        SeriesRoundDb.insertBracket(bracketToInsert)
        SeriesRoundDb.getSeriesRound(1) must beSome(bracketToInsert)
      }
  }
}
