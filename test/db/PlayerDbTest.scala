package db


import models.{Player, Ranks}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.Await
import scala.concurrent.duration.Duration

@RunWith(classOf[JUnitRunner])
class PlayerDbTest extends Specification{
/*
  "PlayerDb" should {
    def initPlayerDb = {
      val appBuilder = new GuiceApplicationBuilder().build()
      val api = appBuilder.injector.instanceOf[PlayerDb]
    }
    "return a list of all players" in new WithPlayerDbSetup {
      val playerDb = initPlayerDb
        val playerList = List(Player(Some("1"),"Koen","Van Loock", Ranks.D2))
      val players = Await.result(playerDb.getAllPlayers, Duration(3000, "millis"))
       players should beEqualTo(playerList)
    }

    "create a new player" in new WithPlayerDbSetup{
      val playerToInsert = Player(Some("2"),"Joris","Van Loock", Ranks.Ng)
      val player = Await.result(playerDb.insertPlayer(playerToInsert),Duration(3000, "millis"))
      player must beEqualTo(Player(Some("2"), playerToInsert.firstname, playerToInsert.lastname, playerToInsert.rank))
    }

    "update a player" in new WithPlayerDbSetup {
      val playerToUpdate = Player(Some("1"),"Koen", "Van Loock", Ranks.Rec)
      Await.result(playerDb.updatePlayer(playerToUpdate, "1"), Duration(3000, "millis"))
      val player = Await.result(playerDb.getPlayer("1"), Duration(3000, "millis"))
      player.get.rank.value must beEqualTo(0)
    }

    "delete a player" in new WithPlayerDbSetup{
      Await.result(playerDb.deletePlayer("1"), Duration(3000, "millis"))
      val player  = Await.result(playerDb.getPlayer("1"), Duration(3000, "millis"))
      player must beNone
    }

  }*/
}
