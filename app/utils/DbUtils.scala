package utils

import java.time.LocalDate

import org.joda.time.DateTime
import reactivemongo.api.{DefaultDB, MongoDriver}
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author Koen Van Loock
 * @version 1.0 13/06/2015 22:52
 */
object DbUtils {
  
  def dbDateToLocalDate(date: DateTime): LocalDate =  LocalDate.of(date.getYear, date.getMonthOfYear, date.getDayOfMonth)

  def localDateToString(localDate: LocalDate): String =
    "%4d-%02d-%02d".format(localDate.getYear, localDate.getMonthValue, localDate.getDayOfMonth)


}
