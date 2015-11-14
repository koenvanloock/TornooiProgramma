package db

import java.time.LocalDate

import helpers.{WithDbTestTournament, WithDbTestSeries}
import models.Tournament
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.test.WithApplication

import scala.concurrent.Await
import scala.concurrent.duration.Duration


@RunWith(classOf[JUnitRunner])
class TournamentDbTest extends Specification {
  val tournamentDb = new TournamentDb
  "TournamentDb" should {
    "get a tournament by id" in new WithDbTestTournament {
      val tournamentToGet = Tournament(Some("1"),"Clubkampioenschap",LocalDate.of(2015,5,12),0,hasMultipleSeries = false,showClub = false)
      val retrievedTournament = Await.result(tournamentDb.getTournament("1"), Duration(3000, "millis"))
      retrievedTournament must beSome(tournamentToGet)
    }

    "return a list of all tournaments" in new WithDbTestTournament {
      val tournamentList = Await.result(tournamentDb.getTournaments, Duration(3000, "millis"))
      tournamentList must beEqualTo(List(Tournament(Some("1"),"Clubkampioenschap",LocalDate.of(2015,5,12),0,hasMultipleSeries = false,showClub = false)))
    }

    "insert a tournament" in new WithApplication{
      val rowId = Await.result(tournamentDb.insertTournament(Tournament(None, "Klaaskampioenschap",LocalDate.of(2015,11,23), 0, hasMultipleSeries = false, showClub = false)), Duration(3000, "millis"))
      val insertedTournament = Await.result(tournamentDb.getTournament(rowId), Duration(3000, "millis"))
      insertedTournament must beSome( Tournament(Some("2"), "Klaaskampioenschap",LocalDate.of(2015,11,23), 0, hasMultipleSeries = false, showClub = false))
    }

    "update a tournament" in new WithDbTestTournament {
      val tournamentToUpdate = Tournament(Some("1"), "Paaskampioenschap", LocalDate.of(2016,4,1), 1, hasMultipleSeries = false,showClub = false)
      Await.result(tournamentDb.updateTournament(tournamentToUpdate), Duration(3000, "millis"))

      val updatedTournament  = Await.result(tournamentDb.getTournament("1") , Duration(3000, "millis"))
        updatedTournament must beSome(tournamentToUpdate)
    }

    "delete a tournament" in new WithDbTestTournament{
      Await.result(tournamentDb.deleteTournament("1"), Duration(3000, "millis"))
      val deletedTournament = Await.result(tournamentDb.getTournament("1"), Duration(3000, "millis"))
      deletedTournament must beNone
    }

  }
}
