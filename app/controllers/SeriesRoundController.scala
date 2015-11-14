package controllers

import db.SeriesRoundDb
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Controller, Action}

class SeriesRoundController extends Controller with TournamentWrites{
    def createSeriesRound = Action{ request =>

      request.body.asJson.map{ json =>
        val seriesRound = parseRoundFromJson(json)
        // seriesRoundDB.
        Ok(Json.toJson(seriesRound))
      }.getOrElse(BadRequest)
    }

  def updateSeriesRound = Action {request =>
    request.body.asJson.map{ json =>
    val seriesRound = parseRoundFromJson(json)
    // seriesRoundDB.
    Ok(Json.toJson(seriesRound))
    }.getOrElse(BadRequest)
  }

  def parseRoundFromJson(jsonRound: JsValue): Option[SeriesRound] =
    (jsonRound \ "roundType").as[String] match{
      case "B" => parseBracketFromJson(jsonRound)
      case "R" => parseRobinFromJson(jsonRound)
    }
}



sealed trait SeriesRound

case class SiteBracketRound(seriesRoundId: Option[Int], numberOfBracketRounds: Int, roundType: String, roundNr: Int) extends SeriesRound

case class RobinRound(seriesRoundId: Option[Int], numberOfRobins: Int, roundType: String, roundNr: Int) extends SeriesRound
