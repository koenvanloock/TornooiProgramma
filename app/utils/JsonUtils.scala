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

import play.api.libs.json.{Json, JsValue, Writes}

object ListWrites {
  def listToJson[W: Writes]: List[W] => JsValue = { ws =>
    Json.toJson(ws.map(Json.toJson(_)))
  }

  implicit class JsonOps(jsonObject: Json.type) {
    def listToJson[W: Writes](ws: List[W]) = ListWrites.listToJson.apply(ws)
  }

}
