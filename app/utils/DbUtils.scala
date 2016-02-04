package utils

import java.time.LocalDate
import java.util.UUID

import org.joda.time.DateTime

object DbUtils {
  
  def dbDateToLocalDate(date: DateTime): LocalDate =  LocalDate.of(date.getYear, date.getMonthOfYear, date.getDayOfMonth)

  def localDateToString(localDate: LocalDate): String =
    "%4d-%02d-%02d".format(localDate.getYear, localDate.getMonthValue, localDate.getDayOfMonth)


  def generateId: String = UUID.randomUUID().toString
}
