package controller

import java.time.LocalDate

import controllers.{TournamentWrites, TournamentController}
import db.TournamentDb
import helpers.{WithDbTestTournament, WithDbTestSeries}
import models.Tournament
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.Logger
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.FakeRequest

import scala.concurrent.Await
import scala.concurrent.duration.Duration

@RunWith(classOf[JUnitRunner])
class TournamentControllerTest extends Specification with TournamentWrites {
  val tournamentDb = new TournamentDb

  "TournamentController" should {
      "return tournament 1" in new WithDbTestSeries {
         val getTournament = route(FakeRequest(GET,"/tournament/1")).get
          status(getTournament) must beEqualTo(OK)
      }

      "return a list of all tournaments" in new WithDbTestTournament {
        val getTournaments = route(FakeRequest(GET,"/tournaments")).get
        status(getTournaments) must beEqualTo(OK)
        (contentAsJson(getTournaments) \\ "tournamentName").head.as[String] must beEqualTo("Clubkampioenschap")
        contentAsString(getTournaments) must contain("[")

      }

      "parse a tournament from a valid json" in {
        val tournamentController = new TournamentController
        val tournament = Tournament(None, "Kapels Kampioenschap", LocalDate.of(2015,9,6), 2, hasMultipleSeries = true, showClub = false)
        Logger.info(tournament.toString)
        val json = Json.toJson(tournament)
        Logger.debug(json.toString())
        tournamentController.parseTournament(json) must beSome(tournament)
      }

    "return None when parsing an invalid json" in {
      val tournamentController = new TournamentController
      val json = Json.parse("""{"key":"not going to work now is it?"}"""")
      tournamentController.parseTournament(json) must beNone
    }

      "insert a new tournament on valid request" in new WithDbTestTournament {
        val insertTournament = route(FakeRequest(POST,"/tournament"), Json.parse("""{"tournamentId":null,"tournamentName":"Kapels Kampioenschap","tournamentDate":{"day":6,"month":9,"year":2015},"maximumNumberOfSeriesEntries":2,"hasMultipleSeries":true,"showClub":false}"""")).get
        status(insertTournament) must beEqualTo(OK)
        val tournament = Await.result(tournamentDb.getTournament("2"), Duration(3000, "milllis"))
        tournament.get.tournamentDate must beEqualTo(LocalDate.of(2015,9,6))
      }

      "return Bad Request on invalid request" in new WithDbTestSeries{
        val insertTournament = route(FakeRequest(POST,"/tournament"), Json.parse("""{"someBadJson":"someBadValue"}"""")).get
        status(insertTournament) must beEqualTo(BAD_REQUEST)
      }

      "update a tournament on a valid put request" in new WithDbTestSeries {
        val updateTournament = route(FakeRequest(PUT, "/tournament/1"), Json.parse("""{"tournamentId":1,"tournamentName":"Kapels Kampioenschap","tournamentDate":{"day":6,"month":9,"year":2015},"maximumNumberOfSeriesEntries":2,"hasMultipleSeries":true,"showClub":false}"""")).get
        status(updateTournament) must beEqualTo(OK)
        val tournament  = Await.result(tournamentDb.getTournament("1"), Duration(3000, "millis"))
        tournament.get.tournamentName must beEqualTo("Kapels Kampioenschap")
      }

      "return Bad Request when an invalid request is put" in new WithDbTestSeries {
        val updateTournament = route(FakeRequest(PUT, "/tournament/1"), Json.parse("""{"someBadJson":"someBadValue"}"""")).get
        status(updateTournament) must beEqualTo(BAD_REQUEST)
      }

      "delete a tournament" in new WithDbTestSeries {
        val deleteTournament = route(FakeRequest(DELETE,"/tournament/1")).get
        status(deleteTournament) must beEqualTo(OK)

      }

  }
}
