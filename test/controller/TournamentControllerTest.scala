package controller

import java.time.LocalDate

import controllers.{TournamentWrites, TournamentController}
import db.slick.{SeriesDb, TournamentDb}
import models.Tournament
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.Logger
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{WithApplication, FakeRequest}
import helpers.TestConstants._


import scala.concurrent.Await
import scala.concurrent.duration.Duration

@RunWith(classOf[JUnitRunner])
class TournamentControllerTest extends Specification with TournamentWrites {

  def initTournamentDb = {
    val appBuilder = new GuiceApplicationBuilder().build()
    val db = appBuilder.injector.instanceOf[TournamentDb]
    db
  }

  def initSeriesDb = {
    val appBuilder = new GuiceApplicationBuilder().build()
    appBuilder.injector.instanceOf[SeriesDb]
  }

  "TournamentController" should {
      "return tournament" in new WithApplication {
        val tournamentDb = initTournamentDb
        Await.result(tournamentDb.deleteAll, DEFAULT_DURATION)

        val existingId = Await.result(tournamentDb.insertTournament(Tournament(None, "Kapels Kampioenschap", LocalDate.of(2016,1,12), 1,false, false)), DEFAULT_DURATION).get.tournamentId.get
         val getTournament = route(FakeRequest(GET,"/tournament/"+ existingId)).get
          status(getTournament) must beEqualTo(OK)
      }

      "return a list of all tournaments" in new WithApplication {

        val tournamentDb = initTournamentDb
        Await.result(tournamentDb.deleteAll, DEFAULT_DURATION)

        Await.result(tournamentDb.insertTournament(Tournament(None, "Clubkampioenschap", LocalDate.of(2016,1,12), 1,false, false)), DEFAULT_DURATION)
        val getTournaments = route(FakeRequest(GET,"/tournaments")).get
        status(getTournaments) must beEqualTo(OK)
        (contentAsJson(getTournaments) \\ "tournamentName").head.as[String] must beEqualTo("Clubkampioenschap")
        contentAsString(getTournaments) must contain("[")

      }

      "parse a tournament from a valid json" in new WithApplication{

        val tournamentController = new TournamentController(initTournamentDb, initSeriesDb)
        val tournament = Tournament(None, "Kapels Kampioenschap", LocalDate.of(2015,9,6), 2, hasMultipleSeries = true, showClub = false)
        Logger.info(tournament.toString)
        val json = Json.toJson(tournament)
        Logger.debug(json.toString())
        tournamentController.parseTournament(json) must beSome(tournament)
      }

    "return None when parsing an invalid json" in {
      val tournamentController = new TournamentController(initTournamentDb, initSeriesDb)
      val json = Json.parse("""{"key":"not going to work now is it?"}"""")
      tournamentController.parseTournament(json) must beNone
    }

      "insert a new tournament on valid request" in new WithApplication {
        val tournamentDb = initTournamentDb
        val insertTournament = route(FakeRequest(POST,"/tournament"), Json.parse("""{"tournamentName":"Kapels Kampioenschap","tournamentDate":{"day":6,"month":9,"year":2015},"maximumNumberOfSeriesEntries":2,"hasMultipleSeries":true,"showClub":false}"""")).get
        status(insertTournament) must beEqualTo(OK)
      }

      "return Bad Request on invalid request" in new WithApplication{
        val insertTournament = route(FakeRequest(POST,"/tournament"), Json.parse("""{"someBadJson":"someBadValue"}"""")).get
        status(insertTournament) must beEqualTo(BAD_REQUEST)
      }

      "update a tournament on a valid put request" in new WithApplication {
        val tournamentDb = initTournamentDb
        Await.result(tournamentDb.deleteAll, DEFAULT_DURATION)
        val insertedTournamentId = Await.result(tournamentDb.insertTournament(Tournament(None, "Een kampioenschap", LocalDate.of(2015,12,7), 1,true, true)), DEFAULT_DURATION).get.tournamentId.get
        val updateTournament = route(FakeRequest(PUT, "/tournament/"+insertedTournamentId), Json.parse(s"""{"tournamentId":"$insertedTournamentId","tournamentName":"Kapels Kampioenschap","tournamentDate":{"day":6,"month":9,"year":2015},"maximumNumberOfSeriesEntries":2,"hasMultipleSeries":true,"showClub":false}"""")).get
        status(updateTournament) must beEqualTo(OK)

        val tournament  = Await.result(tournamentDb.getTournament(insertedTournamentId), Duration(3000, "millis"))
        tournament.get.tournamentName must beEqualTo("Kapels Kampioenschap")
      }

      "return Bad Request when an invalid request is put" in new WithApplication {
        val updateTournament = route(FakeRequest(PUT, "/tournament/1"), Json.parse("""{"someBadJson":"someBadValue"}"""")).get
        status(updateTournament) must beEqualTo(BAD_REQUEST)
      }

      "delete a tournament" in new WithApplication {
        val deleteTournament = route(FakeRequest(DELETE,"/tournament/1")).get
        status(deleteTournament) must beEqualTo(OK)

      }

  }
}
