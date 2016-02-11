package db

import java.time.LocalDate

import db.slick.TournamentDb
import models.Tournament
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.WithApplication

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import helpers.TestConstants._

@RunWith(classOf[JUnitRunner])
class TournamentDbTest extends Specification {
  def initTournamentDb = {
    val appBuilder = new GuiceApplicationBuilder().build()
    val db = appBuilder.injector.instanceOf[TournamentDb]
    Await.result(db.deleteAll, DEFAULT_DURATION)
    db
  }

  "TournamentDb" should {
    "get a tournament by id" in new WithApplication {
      val tournamentDb = initTournamentDb

      val tournamentToGet = Tournament(None,"Clubkampioenschap",LocalDate.of(2015,5,12),0,hasMultipleSeries = false,showClub = false)
      val id = Await.result(tournamentDb.insertTournament(tournamentToGet), DEFAULT_DURATION).get.tournamentId
      val retrievedTournament = Await.result(tournamentDb.getTournament(id.get), Duration(3000, "millis"))
      retrievedTournament must beSome(tournamentToGet.copy(tournamentId = id))
    }

    "return a list of all tournaments" in new WithApplication {
      val tournamentDb = initTournamentDb
      val tournament = Tournament(None,"Clubkampioenschap",LocalDate.of(2015,5,12),0,hasMultipleSeries = false,showClub = false)
      val insertedTournament = Await.result(tournamentDb.insertTournament(tournament), DEFAULT_DURATION).get
      val tournamentList = Await.result(tournamentDb.getAllTournaments, Duration(3000, "millis"))
      tournamentList must beEqualTo(List(insertedTournament))
    }

    "insert a tournament" in new WithApplication{
      val tournamentDb = initTournamentDb

      val rowId = Await.result(tournamentDb.insertTournament(Tournament(None, "Klaaskampioenschap",LocalDate.of(2015,11,23), 0, hasMultipleSeries = false, showClub = false)), Duration(3000, "millis")).get.tournamentId.get
      val insertedTournament = Await.result(tournamentDb.getTournament(rowId), Duration(3000, "millis"))
      insertedTournament must beSome( Tournament(Some(rowId), "Klaaskampioenschap",LocalDate.of(2015,11,23), 0, hasMultipleSeries = false, showClub = false))
    }

    "update a tournament" in new WithApplication {
      val tournamentDb = initTournamentDb
      val insertedTournament = Await.result( tournamentDb.insertTournament(Tournament(None, "Clubkampioenschap", LocalDate.of(2015,4,15), 1, hasMultipleSeries=true, showClub = true)) , DEFAULT_DURATION)
      val tournamentToUpdate = Tournament(Some("1"), "Paaskampioenschap", LocalDate.of(2016,4,1), 1, hasMultipleSeries = false,showClub = false)
      Await.result(tournamentDb.updateTournament(tournamentToUpdate), Duration(3000, "millis"))

      val updatedTournament  = Await.result(tournamentDb.getTournament("1") , Duration(3000, "millis"))
        updatedTournament must beSome(tournamentToUpdate)
    }

    "delete a tournament" in new WithApplication{
      val tournamentDb = initTournamentDb

      Await.result(tournamentDb.deleteTournament("1"), Duration(3000, "millis"))
      val deletedTournament = Await.result(tournamentDb.getTournament("1"), Duration(3000, "millis"))
      deletedTournament must beNone
    }

  }
}
