package controller

import controllers.TournamentWrites
import db.slick.SeriesDb
import models.TournamentSeries
import org.specs2.mutable.Specification
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{WithApplication, FakeRequest}

import helpers.TestConstants._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Await

class SeriesControllerTest extends Specification with TournamentWrites{
  val initSeriesDb =  {
    val appBuilder = new GuiceApplicationBuilder().build()
    val db = appBuilder.injector.instanceOf[SeriesDb]
    Await.result(db.deleteAll, DEFAULT_DURATION)
    db
  }

  "SeriesController" should {
    "insert a new series with correct json" in new WithApplication{
      val insertSeries = route(FakeRequest(POST, "/series"), Json.parse("""{"seriesName": "TestReeks", "seriesColor": "#ffffff", "setTargetScore": 21, "numberOfSetsToWin": 2, "playingWithHandicaps": false, "extraHandicapForRecs": 0,"showReferees": false,"tournamentId": "1"}""")).get

      status(insertSeries) must equalTo(CREATED)
      println(contentAsString(insertSeries))
      //contentType(insertSeries) must beSome.which(_ == "application/json")
    }

    "insert a new series with incorrect json" in new WithApplication{
      val insertSeries = route(FakeRequest(POST, "/series"), Json.parse("""{"seriesName": "TestReeks", "slechte": "json"}""")).get
      status(insertSeries) must equalTo(BAD_REQUEST)
    }

    "update a series with correct json" in new WithApplication{
      val db = initSeriesDb
      val series = Await.result(db.insertSeries(TournamentSeries(Some("1"),"Open met voorgift","#ffffff",2,21,playingWithHandicaps = true,0,showReferees = false,1,"1")), DEFAULT_DURATION)
      val updateSeries = route(FakeRequest(PUT, "/series/"+series.get.seriesId.get), Json.parse("""{"seriesName": "Changed", "seriesColor": "#ffffff", "setTargetScore": 21, "numberOfSetsToWin": 2, "playingWithHandicaps": false, "showReferees": false,"extraHandicapForRecs": 0,"tournamentId": "1"}""")).get

      status(updateSeries) must equalTo(NO_CONTENT)
      //contentType(updateSeries) must beSome.which(_ == "application/json")
    }
  }
}
