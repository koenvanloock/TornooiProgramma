package controller

import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._
import utils.WebTokenUtils

@RunWith(classOf[JUnitRunner])
class AuthControllerTest extends Specification{

    "AuthController" should {

      "return a token when a user and password match with a db record" in new WithApplication {
        val loginRequest = route(FakeRequest(POST, "/login"), Json.parse( """{"username":"admin","password":"admin"}""")).get

        status(loginRequest) must equalTo(OK)
        contentType(loginRequest) must beSome[String]
        WebTokenUtils.validateToken(contentAsString(loginRequest)) must beTrue
      }

      "return a Badrequest when a user doesn't apear in the db" in new WithApplication {
        val loginRequest = route(FakeRequest(POST, "/login"), Json.parse( """{"username":"onbestaand","password":"weetiknietmeer"}""")).get

        status(loginRequest) must equalTo(BAD_REQUEST)
      }
    }

}
