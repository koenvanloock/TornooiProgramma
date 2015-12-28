package utils

import java.time.{LocalDateTime, ZoneOffset}

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import models.AuthUser
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.test.WithApplication

@RunWith(classOf[JUnitRunner])
class WebTokenUtilsTest extends Specification {

  "WebTokenUtils" should {

    "create a valid JWT from a User" in new WithApplication {
      WebTokenUtils.validateToken(WebTokenUtils.createJWT(AuthUser(None, "admin", "admin", Some(1)))) must beTrue
    }

    "block an expired token" in new WithApplication {
      WebTokenUtils.validateToken(expiredJWT) must beFalse
    }

    "the expired token returns true on isExpired" in new WithApplication {
      WebTokenUtils.isExpired(expiredJWT) must beTrue
    }
  }

  def expiredJWT: String = {
    val header = JwtHeader("HS256")
    val claimsSet = JwtClaimsSet(
      Map(
        "username" -> "admin",
        "role" -> "administrator",
        "exp" -> LocalDateTime.now.minusSeconds(12 * 3600).toEpochSecond(ZoneOffset.UTC))
    )
    JsonWebToken(header, claimsSet, "PipokaFTW")
  }
}
