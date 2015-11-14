package controller

import helpers.WithDbTestSeries
import org.specs2.mutable.Specification
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.FakeRequest

class SeriesControllerTest extends Specification{
  "SeriesController" should {
    "insert a new series with correct json" in new WithDbTestSeries{
      val insertSeries = route(FakeRequest(POST, "/series"), Json.parse("""{"seriesName": "TestReeks", "seriesColor": "#ffffff", "setTargetScore": 21, "numberOfSetsToWin": 2, "playingWithHandicaps": false, "extraHandicapForRecs": 0,"showReferees": false,"tournamentId": 1}""")).get

      status(insertSeries) must equalTo(CREATED)
      contentType(insertSeries) must beSome.which(_ == "application/json")
    }

    "insert a new series with incorrect json" in new WithDbTestSeries{
      val insertSeries = route(FakeRequest(POST, "/series"), Json.parse("""{"seriesName": "TestReeks", "slechte": "json"}""")).get
      status(insertSeries) must equalTo(BAD_REQUEST)
    }

    "update a series with correct json" in new WithDbTestSeries{
      val updateSeries = route(FakeRequest(PUT, "/series/1"), Json.parse("""{"seriesName": "Changed", "seriesColor": "#ffffff", "setTargetScore": 21, "numberOfSetsToWin": 2, "playingWithHandicaps": false, "showReferees": false,"extraHandicapForRecs": 0,"tournamentId": 1}""")).get

      status(updateSeries) must equalTo(NO_CONTENT)
      contentType(updateSeries) must beSome.which(_ == "application/json")
    }
  }
}
