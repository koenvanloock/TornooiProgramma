package utils

import java.time.LocalDate

import play.api.libs.json.JsValue


object JsonUtils {
  def parseJsonToLocalDate(dateJson: JsValue) = {
    for{
      day <- (dateJson \ "day").asOpt[Int]
      month <- (dateJson \ "month").asOpt[Int]
      year <- (dateJson \ "year").asOpt[Int]

    } yield LocalDate.of(year, month, day)
  }

}
