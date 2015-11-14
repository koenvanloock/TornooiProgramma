package utils

import java.time.LocalDate

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class DbUtilsTest extends Specification{

  "DbUtils" should {

    "convert a joda DateTime to a java 8 LocalDate" in {
      val parser = DateTimeFormat.forPattern("dd/MM/yyyy")
      val date = DateTime.parse("07/12/1988", parser)
      DbUtils.dbDateToLocalDate(date) must beEqualTo(LocalDate.of(1988,12,7))
    }

    "convert a LocalDate to a yyyy-MM-dd string" in {
      DbUtils.localDateToString(LocalDate.of(2015,10,1)) must beEqualTo("2015-10-01")
    }
  }
}
