package db

import db.slick.SeriesDb
import models.TournamentSeries
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.WithApplication

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import helpers.TestConstants._

@RunWith(classOf[JUnitRunner])
class SeriesDbTest extends Specification{
  "SeriesDb" should {
    def initSeriesDb = {
    val appBuilder = new GuiceApplicationBuilder().build()
     val db = appBuilder.injector.instanceOf[SeriesDb]
      Await.result(db.deleteAll, DEFAULT_DURATION)
      db
  }


    "insert a new series" in new WithApplication {
      val seriesDb = initSeriesDb

      val tournamentSeries = Await.result(seriesDb.insertSeries(TournamentSeries(None,"Open met voorgift","#ffffff",2, 21,playingWithHandicaps = true, 0,showReferees = true,1)), DEFAULT_DURATION)
      println(tournamentSeries)
      val insertedSeries = Await.result(seriesDb.getSeries(tournamentSeries.get.seriesId.get), DEFAULT_DURATION)

        insertedSeries must beEqualTo(tournamentSeries)
    }

    "update a series" in new WithApplication {
      val seriesDb = initSeriesDb
      val series = TournamentSeries(Some(1),"Open zonder voorgift","#ffffff",2, 21,playingWithHandicaps = true, 0,showReferees = true,1)
      val insertedSeries = Await.result(seriesDb.insertSeries(series), DEFAULT_DURATION)
      val tournamentSeries = TournamentSeries(insertedSeries.get.seriesId,"Masters","#ffffff",2, 21,playingWithHandicaps = true, 0,showReferees = false,1)
      Await.result(seriesDb.updateSeries(tournamentSeries), DEFAULT_DURATION)
      val updatedSeries = Await.result(seriesDb.getSeries(insertedSeries.get.seriesId.get), DEFAULT_DURATION)
      updatedSeries must beSome(tournamentSeries)
    }

    "delete a series" in new WithApplication {
      val seriesDb = initSeriesDb

      val series = Await.result(seriesDb.insertSeries(TournamentSeries(Some(1),"Open zonder voorgift","#ffffff",2, 21,playingWithHandicaps = true, 0,showReferees = true,1)), DEFAULT_DURATION)
      val affectedRows = Await.result(seriesDb.deleteSeries(series.get.seriesId.get), DEFAULT_DURATION)
      val deletedSeries = Await.result(seriesDb.getSeries(series.get.seriesId.get), DEFAULT_DURATION)
      //affectedRows must beEqualTo(1)
      deletedSeries must beNone
    }

    "return a list of all series of a tournament" in new WithApplication {
      val seriesDb = initSeriesDb
      val series = TournamentSeries(Some(1),"Open zonder voorgift","#ffffff",2, 21,playingWithHandicaps = true, 0,showReferees = true,1)
      val insertedSeries = Await.result(seriesDb.insertSeries(series), DEFAULT_DURATION)
      val tournamentList = Await.result(seriesDb.getSeriesListOfTournament(1), DEFAULT_DURATION)
      tournamentList must beEqualTo(List(insertedSeries.get))
    }
  }
}
