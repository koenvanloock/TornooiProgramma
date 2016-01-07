package db

import db.slick.SeriesPlayersDb
import models.{SeriesPlayer, Ranks}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.inject.guice.GuiceApplicationBuilder
import helpers.TestConstants._
import play.api.test.{WithApplication, PlaySpecification}

import scala.concurrent.Await
import scala.concurrent.duration.Duration


@RunWith(classOf[JUnitRunner])
class SeriesPlayerDbTest extends PlaySpecification {
  val appBuilder = new GuiceApplicationBuilder().build()
  val seriesPlayerDb = appBuilder.injector.instanceOf[SeriesPlayersDb]

  "SeriesPlayerDb" should {
    "insert a seriesPlayer" in new WithApplication {
    }

    "delete a seriesPlayer" in new WithApplication {

    }

    "get all players of series 1" in new WithApplication {
    }

    "get all series of player 1 in tournament 1" in new WithApplication{

    }

  }
}
