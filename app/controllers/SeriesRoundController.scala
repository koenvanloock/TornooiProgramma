package controllers

import com.google.inject.Inject
import db.slick.{RobinDb, SeriesPlayersDb, SeriesRoundDb}
import models.{SiteBracketRound, RobinRound, SeriesRound}
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Controller, Action}
import utils.Draw
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SeriesRoundController @Inject()(val seriesRoundDb: SeriesRoundDb, seriesPlayerDb: SeriesPlayersDb, robinDb: RobinDb) extends Controller with TournamentWrites{
    def createSeriesRound = Action.async{ request =>

      request.body.asJson.flatMap{ json =>
        parseRoundFromJson(json)
        .map{
          round => seriesRoundDb.insertSeriesRound(round)
            .map{ insertedRoundOpt =>
              Logger.info(insertedRoundOpt.toString)
              insertedRoundOpt
              .map(seriesRound => Ok(Json.toJson(seriesRound)))
              .getOrElse(InternalServerError)
            }
      }
      }.getOrElse(Future(BadRequest))
    }

  def updateSeriesRound = Action.async {request =>
    request.body.asJson.flatMap{ json =>
    parseRoundFromJson(json)
      .map { seriesRound => {
        Logger.debug("seriesRound="+seriesRound.toString)
        seriesRoundDb.updateSeriesRound(seriesRound).map { _.map{ round =>
          Ok(Json.toJson(round))
        }.getOrElse(InternalServerError)
        }
      }
    }
    }.getOrElse(Future(BadRequest))
  }

  def parseRoundFromJson(jsonRound: JsValue): Option[SeriesRound] =
    (jsonRound \ "roundType").as[String] match{
      case "B" => parseBracketFromJson(jsonRound)
      case "R" => parseRobinFromJson(jsonRound)
    }

  def parseBracketFromJson(jsonBracket: JsValue): Option[SiteBracketRound] = for{
    numberOfBracketRounds <- (jsonBracket \ "numberOfBracketRounds").asOpt[Int]
    seriesId <- (jsonBracket \ "seriesId").asOpt[Int]

  }yield SiteBracketRound((jsonBracket \ "seriesRoundId").asOpt[Int], numberOfBracketRounds, "B", seriesId, (jsonBracket \ "roundNr").asOpt[Int].getOrElse(0))

  def parseRobinFromJson(jsonRobin: JsValue): Option[RobinRound] = for{
    numberOfBracketRounds <- (jsonRobin \ "numberOfRobinGroups").asOpt[Int]
    seriesId <- (jsonRobin \ "seriesId").asOpt[Int]
  }yield RobinRound((jsonRobin \ "seriesRoundId").asOpt[Int], numberOfBracketRounds, "R", seriesId,(jsonRobin \ "roundNr").asOpt[Int].getOrElse(0))

  def getRoundsOfSeries(seriesId: Int) = Action.async{
    seriesRoundDb.getRoundsListOfSeries(seriesId).map(roundList => Ok(Json.toJson(roundList)))
  }

  def drawSeries(seriesRoundId: Int) = Action.async {
    // deleteRound

    seriesRoundDb.getSeriesRound(seriesRoundId).flatMap {
      case Some(seriesRound) =>
        seriesRound match {
          case r: RobinRound =>
            seriesPlayerDb.getPlayersOfSeries(r.seriesId).flatMap { seriesPlayers =>
              val robinList = Future.sequence(Draw.splitPlayersInGroups(seriesPlayers, r.numberOfRobins).map { playerList =>
                robinDb.createRobin(r.seriesRoundId.get, playerList, 2, 21)
              })
              robinList.map( list => Ok(Json.toJson(list)))
            }
          case bracket:SiteBracketRound => Future(Ok)
        }
      case None => Future(BadRequest)
    }
  }
}








