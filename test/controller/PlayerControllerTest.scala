package controller

import db.slick.PlayerDb
import models.{Rank, Player, Ranks}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.{FakeRequest, WithApplication, PlaySpecification}
import play.libs.Json._
import utils.RankConverter
import helpers.TestConstants._

import scala.concurrent.Await


@RunWith(classOf[JUnitRunner])
class PlayerControllerTest extends PlaySpecification {
  implicit val rankWrites = Json.writes[Rank]
  implicit val playerWrites = Json.writes[Player]

  val initPlayerDb =  {
    val appBuilder = new GuiceApplicationBuilder().build()
    val db = appBuilder.injector.instanceOf[PlayerDb]
    Await.result(db.deleteAll, DEFAULT_DURATION)
    db
  }

  "PlayerController" should {


    "return all players on /all" in new WithApplication{
      val playerDb = initPlayerDb
      val playerId = Await.result(playerDb.insertPlayer(Player(None, "Koen", "Van Loock", Ranks.D2)), DEFAULT_DURATION).get.playerId.get
      val allPlayers = route(FakeRequest(GET, "/players")).get

      status(allPlayers) must equalTo(OK)
      contentType(allPlayers) must beSome.which(_ == "application/json")
      contentAsString(allPlayers) must contain (s"""{"playerId":"$playerId","firstname":"Koen","lastname":"Van Loock","rank":""")
    }

    "retun bad request on bad json insertPlayer input" in new WithApplication {
      val postPlayer = route(FakeRequest(POST, "/player"),Json.parse("""{"key": "this isn't going to work"}""")).get
      status(postPlayer) must equalTo(BAD_REQUEST)
    }

    "return created on valid json insertPlayer input" in new WithApplication{
      val postPlayer= route(FakeRequest(POST, "/player"), Json.parse("""{"firstname":"Joske","lastname":"Vermeulen","rank":{"name":"E2","value":5}}""")).get
      status(postPlayer) must equalTo(CREATED)
    }

    "return ok with valid delete" in new WithApplication {
      val deletePlayer = route(FakeRequest(DELETE, "/player/1")).get
      status(deletePlayer) must equalTo(OK)
    }

    "return all ranks" in new WithApplication{
       val getRanks = route(FakeRequest(GET, "/ranks")).get

      status(getRanks) must beEqualTo(OK)
      contentAsString(getRanks).contains("""{"name":"C4","value":12}""") must beTrue
      contentAsString(getRanks).contains("[") must beTrue
    }

  }
}
