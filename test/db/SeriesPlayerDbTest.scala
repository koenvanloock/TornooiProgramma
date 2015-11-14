package db

import controllers.SeriesPlayer
import helpers.WithDbTestSeriesPlayer
import models.Ranks
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SeriesPlayerDbTest extends Specification {
  "SeriesPlayerDb" should {
    "insert a seriesPlayer" in new WithDbTestSeriesPlayer {
      val seriesPlayerToInsert = SeriesPlayer(2, "Koen", "Van Loock", "ttc pipoka", Ranks.D2, 0, 0, 0, 0, 0, 0)
      SeriesPlayerDb.insertSeriesPlayer(seriesPlayerToInsert, 1)
      SeriesPlayerDb.getSeriesPlayer(2) must beSome(seriesPlayerToInsert)
    }

    "update a seriesPlayers' general info" in new WithDbTestSeriesPlayer {
      val seriesPlayerToUpdate = SeriesPlayer(1, "Hanz", "DE KANIBAAL", "TTC Pipoka", Ranks.D4, 0, 0, 0, 0, 0, 0)
      SeriesPlayerDb.updateSeriesPlayerGeneral(seriesPlayerToUpdate)
      SeriesPlayerDb.getSeriesPlayer(1) must beSome(seriesPlayerToUpdate)
    }

    "update a seriesPlayers' scores" in new WithDbTestSeriesPlayer {
      val seriesPlayerToUpdate = SeriesPlayer(1, "Hanz", "DE KANIBAAL", "TTC Pipoka", Ranks.D4, 1, 0, 2, 0, 42, 15)
      SeriesPlayerDb.updateSeriesPlayerScores(seriesPlayerToUpdate)
      SeriesPlayerDb.getSeriesPlayer(1).get.firstname must beEqualTo("Luk")
      SeriesPlayerDb.getSeriesPlayer(1).get.lastname must beEqualTo("Geraets")
      SeriesPlayerDb.getSeriesPlayer(1).get.wonMatches must beEqualTo(1)
      SeriesPlayerDb.getSeriesPlayer(1).get.lostPoints must beEqualTo(15)
    }

    "delete a seriesPlayer" in new WithDbTestSeriesPlayer {
      SeriesPlayerDb.deleteSeriesPlayer(1)
      SeriesPlayerDb.getSeriesPlayer(1) must beNone
    }

    "get all players of series 1" in new WithDbTestSeriesPlayer{
      SeriesPlayerDb.getPlayersOfSeries(1) must beEqualTo(List(SeriesPlayer(1,"Luk","Geraets","TTC Pipoka",Ranks.D4,0,0,0,0,0,0)))
    }

  }
}
