package db

import models.{SeriesPlayer, Ranks}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.inject.guice.GuiceApplicationBuilder
import helpers.TestConstants._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/*
@RunWith(classOf[JUnitRunner])
class SeriesPlayerDbTest extends Specification {
  val appBuilder = new GuiceApplicationBuilder().build()
  implicit val api = appBuilder.injector.instanceOf[ReactiveMongoApi]
  val seriesPlayerDb = new SeriesPlayerDb(reactiveMongoApi = api)
  
  "SeriesPlayerDb" should {
    "insert a seriesPlayer" in new WithDbTestSeriesPlayer {
      val seriesPlayerToInsert = SeriesPlayer("2", "Koen", "Van Loock", "ttc pipoka", Ranks.D2, 0, 0, 0, 0, 0, 0)
      Await.result(seriesPlayerDb.insertSeriesPlayer(seriesPlayerToInsert), DEFAULT_DURATION)
      Await.result(seriesPlayerDb.getSeriesPlayer("2"), DEFAULT_DURATION) must beSome(seriesPlayerToInsert)
    }

    "update a seriesPlayers' general info" in new WithDbTestSeriesPlayer {
      val seriesPlayerToUpdate = SeriesPlayer("1", "Hanz", "DE KANIBAAL", "TTC Pipoka", Ranks.D4, 0, 0, 0, 0, 0, 0)
      Await.result(seriesPlayerDb.updateSeriesPlayerGeneral(seriesPlayerToUpdate), DEFAULT_DURATION)
      Await.result(seriesPlayerDb.getSeriesPlayer("1"), DEFAULT_DURATION) must beSome(seriesPlayerToUpdate)
    }

    "update a seriesPlayers' scores" in new WithDbTestSeriesPlayer {
      val seriesPlayerToUpdate = SeriesPlayer("1", "Hanz", "DE KANIBAAL", "TTC Pipoka", Ranks.D4, 1, 0, 2, 0, 42, 15)
      Await.result(seriesPlayerDb.updateSeriesPlayerScores(seriesPlayerToUpdate), DEFAULT_DURATION)
      val seriesPlayer = Await.result(seriesPlayerDb.getSeriesPlayer("1"), DEFAULT_DURATION).get
        seriesPlayer.firstname must beEqualTo("Luk")
        seriesPlayer.lastname must beEqualTo("Geraets")
        seriesPlayer.wonMatches must beEqualTo(1)
        seriesPlayer.lostPoints must beEqualTo(15)
    }

    "delete a seriesPlayer" in new WithDbTestSeriesPlayer {
      Await.result(seriesPlayerDb.deleteSeriesPlayer("1"), DEFAULT_DURATION)
      Await.result(seriesPlayerDb.getSeriesPlayer("1"), DEFAULT_DURATION) must beNone
    }

    "get all players of series 1" in new WithDbTestSeriesPlayer{
      val seriesPlayers = Await.result(seriesPlayerDb.getPlayersOfSeries("1"), DEFAULT_DURATION)
      seriesPlayers must beEqualTo(List(SeriesPlayer("1","Luk","Geraets","TTC Pipoka",Ranks.D4,0,0,0,0,0,0)))
    }

  }
}*/
