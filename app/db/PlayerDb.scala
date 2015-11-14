package db

import javax.inject.Inject

import controllers.Player
import models.Rank
import play.api.Logger
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Controller
import play.modules.reactivemongo.{MongoController, ReactiveMongoComponents, ReactiveMongoApi}
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.MongoDriver
import reactivemongo.api.commands.WriteResult
import scala.concurrent.ExecutionContext.Implicits.global

import utils.{DbUtils, RankConverter}

import scala.concurrent.Future

class PlayerDb {

  implicit val rankWrites = Json.format[Rank]
  implicit val playerWithIdWrites = Json.format[Player]

  def deletePlayer(playerId: String): Future[WriteResult] = {
    val driver = new MongoDriver
    val connection = driver.connection(List("localhost:27017"))("pipoka_test")
    val db = connection.collection[JSONCollection]("players")
    val result = db
      .remove(Json.obj("playerId" -> playerId))
    driver.close()

    result
  }


/*
  def deletePlayer(playerId: Long) = DB.withConnection{ implicit conn =>
    SQL("""DELETE FROM players WHERE playerId = {playerId}""").on("playerId"-> playerId).executeUpdate()
  }*/

  def updatePlayer(playerToUpdate: Player, playerId: String): Future[WriteResult] = {
    val driver = new MongoDriver
    val connection = driver.connection(List("localhost:27017"))("pipoka_test")
    val db = connection.collection[JSONCollection]("players")
    val result = db
      .update(Json.obj("playerId" -> playerId), playerToUpdate)
    driver.close()
    result
  }


    /*DB.withConnection{ implicit conn =>
    val rows: Int = SQL(
      """
        |UPDATE players
        |SET firstname = {firstname}, lastname = {lastname}, rank = {rankvalue}
        |WHERE playerId={playerId}
      """.stripMargin)
            .on(
              "firstname"-> playerToUpdate.firstname,
              "lastname" -> playerToUpdate.lastname,
              "rankvalue" -> playerToUpdate.rank.value,
              "playerId"-> playerId).executeUpdate()

    rows
  }*/


  def getPlayer(id: String): Future[Option[Player]] = {
    val driver = new MongoDriver
    val connection = driver.connection(List("localhost:27017"))("pipoka_test")
    val db = connection.collection[JSONCollection]("players")
    val result = db
      .find(Json.obj("playerId"-> id))
      .cursor[Player]().collect[List](upTo = 1).map(_.headOption)
    driver.close()
    result
  }


    /*DB.withConnection{ implicit conn =>
        SQL("""SELECT * FROM players WHERE playerId = {id}""").on("id" -> id).apply().headOption.map(parsePlayerFromRow)
    }*/

    def insertPlayer(playerToInsert: Player): Future[Player] = {
      val driver = new MongoDriver
      val connection = driver.connection(List("localhost:27017"))("pipoka_test")
      val db = connection.collection[JSONCollection]("players")

      val result = getCount.flatMap{ count =>
        val playerId = (count + 1).toString
        val player = Player(Some(playerId), playerToInsert.firstname, playerToInsert.lastname, playerToInsert.rank)
        db.insert(player).map(_ => player)
      }

      driver.close()
      result
    }

      /*DB.withConnection{ implicit conn =>
        val id: Option[Long] = SQL(
          """
            |INSERT INTO players(firstname,lastname,rank)
            |VALUES({firstname},{lastname},{rankvalue})
          """.stripMargin)
          .on(  "firstname"-> playerToInsert.firstname,
                "lastname" -> playerToInsert.lastname,
                "rankvalue" -> playerToInsert.rank.value).executeInsert()
      id
    }*/

    def deleteAll = {
      val driver = new MongoDriver
      val connection = driver.connection(List("localhost:27017"))("pipoka_test")
      val db = connection.collection[JSONCollection]("players")
      val result = db.remove(Json.obj())
      driver.close()
      result
    }

    def getCount: Future[Int] = {
      val driver = new MongoDriver
      val connection = driver.connection(List("localhost:27017"))("pipoka_test")
      val db = connection.collection[JSONCollection]("players")
      val result = db.count()
      driver.close()
      result
    }
    def getAllPlayers: Future[List[Player]] = {
      val driver = new MongoDriver
      val connection = driver.connection(List("localhost:27017"))("pipoka_test")
      val db = connection.collection[JSONCollection]("players")
      val result = db.find(Json.obj()).cursor[Player]().collect[List]()
      driver.close()
      result
    }

      /*DB.withConnection{ implicit conn =>
        SQL("SELECT * FROM players").list().map(parsePlayerFromRow)
    }

    def parsePlayerFromRow: (Row) => PlayerWithId = {
        row => PlayerWithId(row[Int]("playerId"), row[String]("firstname"),row[String]("lastname"), RankConverter.getRankOfInt(row[Int]("rank")))
    }*/

}
