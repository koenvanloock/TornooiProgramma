package utils

import java.time.LocalDate

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.Json

@RunWith(classOf[JUnitRunner])
class JsonUtilsTest extends Specification{
    "JsonUtils" should {
      "convert a valid date json to a LocalDate" in {
        val dateJson = Json.parse("""{"day": 24, "month": 8, "year": 2015}""")
        JsonUtils.parseJsonToLocalDate(dateJson) must beSome(LocalDate.of(2015, 8, 24))
      }

      "return None on an invalid json date" in {
        val dateJson = Json.parse("""{"day": 24, "month": 8, "year": "weetikveel"}""")
        JsonUtils.parseJsonToLocalDate(dateJson) must beNone
      }

    }
}
