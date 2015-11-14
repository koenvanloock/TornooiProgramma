package db

import controllers.TournamentSeries
import helpers.WithDbTestSeries
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

import scala.concurrent.Await
import scala.concurrent.duration.Duration

@RunWith(classOf[JUnitRunner])
class SeriesDbTest extends Specification{
  "SeriesDb" should {
    val seriesDb = new SeriesDb

    "insert a new series" in new WithDbTestSeries {
      val tournamentSeries = TournamentSeries("2","Open met voorgift","#ffffff",2, 21,playingWithHandicaps = true, 0,showReferees = true,1, List(), List())
      seriesDb.insertSeries(tournamentSeries)
      val insertedSeries = Await.result(seriesDb.getSeries("2"), Duration(3000, "millis"))
        insertedSeries must beSome(tournamentSeries)
    }

    "update a series" in new WithDbTestSeries {
      val tournamentSeries = TournamentSeries("1","Masters","#ffffff",2, 21,playingWithHandicaps = true, 0,showReferees = false,1, List(), List())
      seriesDb.updateSeries(tournamentSeries, "1")
      val updatedSeries = Await.result(seriesDb.getSeries("1"), Duration(3000, "millis"))
      updatedSeries must beSome(tournamentSeries)
    }

    "delete a series" in new WithDbTestSeries {
      seriesDb.deleteSeries("1")
      val deletedSeries = Await.result(seriesDb.getSeries("1"), Duration(3000, "millis"))
      deletedSeries must beNone
    }

    "return a list of all series of a tournament" in new WithDbTestSeries {
      val tournamentList = Await.result(seriesDb.getSeriesListOfTournament("1"), Duration(3000, "millis"))
      tournamentList must beEqualTo(List(TournamentSeries("1","Open zonder voorgift","#ffffff",2, 21,playingWithHandicaps = true, 0,showReferees = true,1, List(), List())
      ))
    }
  }
}
