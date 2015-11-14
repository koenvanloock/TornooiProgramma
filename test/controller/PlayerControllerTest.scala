package controller

import helpers.WithDbTestUser
import models.Ranks
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication}
import utils.RankConverter

@RunWith(classOf[JUnitRunner])
class PlayerControllerTest extends Specification {

  "UserController" should {

    "return all players on /all" in new WithDbTestUser{
      val allPlayers = route(FakeRequest(GET, "/players")).get

      status(allPlayers) must equalTo(OK)
      contentType(allPlayers) must beSome.which(_ == "application/json")
      contentAsString(allPlayers) must contain ("""{"id":1,"firstname":"Koen","lastname":"Van Loock","rank":""")
    }

    "retun bad request on bad json insertPlayer input" in new WithApplication() {
      val postPlayer = route(FakeRequest(POST, "/player"),Json.parse("""{"key": "this isn't going to work"}""")).get
      status(postPlayer) must equalTo(BAD_REQUEST)
    }

    "return created on valid json insertPlayer input" in new WithApplication{
      val postPlayer= route(FakeRequest(POST, "/player"), Json.parse("""{"firstname":"Joske","lastname":"Vermeulen","rank":{"name":"E2","value":5}}""")).get
      status(postPlayer) must equalTo(CREATED)
    }

    "return the updated player on valid update request" in new WithDbTestUser {
      val putPlayer= route(FakeRequest(PUT, "/player/1"), Json.parse("""{"firstname":"Koen","lastname":"Van Loock","rank":{"name":"REC","value":0}}""")).get
      status(putPlayer) must equalTo(OK)
      contentType(putPlayer) must beSome("application/json")
      RankConverter.getRankOfInt((contentAsJson(putPlayer) \ "rank" \ "value").as[Int]) must beEqualTo(Ranks.Rec)
    }

    "return bad request for update player on invalid update request" in new WithDbTestUser {
      val putPlayer= route(FakeRequest(PUT, "/player/99999"), Json.parse("""{"firstname":"Koen","rank":{"name":"REC","value":0}}""")).get
      status(putPlayer) must equalTo(BAD_REQUEST)
    }

    "return ok with valid delete" in new WithDbTestUser {
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
