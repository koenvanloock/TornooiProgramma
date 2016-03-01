package controllers

import com.google.inject.Inject
import db.slick.{RobinDb, SeriesPlayersDb, SeriesRoundDb}
import models._
import models.RobinMatch._
import play.api.Logger
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.{Controller, Action}
import utils.Draw
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import models.RobinGroupFormat._

class SeriesRoundController @Inject()(val seriesRoundDb: SeriesRoundDb, seriesPlayerDb: SeriesPlayersDb, robinDb: RobinDb) extends Controller with TournamentWrites {
  def createSeriesRound = Action.async { request =>

    request.body.asJson.flatMap { json =>
      parseRoundFromJson(json)
        .map {
          round => seriesRoundDb.insertSeriesRound(round)
            .map { insertedRoundOpt =>
              Logger.info(insertedRoundOpt.toString)
              insertedRoundOpt
                .map(seriesRound => Ok(Json.toJson(seriesRound)))
                .getOrElse(InternalServerError)
            }
        }
    }.getOrElse(Future(BadRequest))
  }

  def updateSeriesRound = Action.async { request =>
    request.body.asJson.flatMap { json =>
      parseRoundFromJson(json)
        .map { seriesRound => {
          Logger.debug("seriesRound=" + seriesRound.toString)
          seriesRoundDb.updateSeriesRound(seriesRound).map {
            _.map { round =>
              Ok(Json.toJson(round))
            }.getOrElse(InternalServerError)
          }
        }
        }
    }.getOrElse(Future(BadRequest))
  }

  def parseRoundFromJson(jsonRound: JsValue): Option[SeriesRound] =
    (jsonRound \ "roundType").as[String] match {
      case "B" => parseBracketFromJson(jsonRound)
      case "R" => parseRobinFromJson(jsonRound)
    }

  def parseBracketFromJson(jsonBracket: JsValue): Option[SiteBracketRound] = for {
    numberOfBracketRounds <- (jsonBracket \ "numberOfBracketRounds").asOpt[Int]
    seriesId <- (jsonBracket \ "seriesId").asOpt[String]

  } yield SiteBracketRound((jsonBracket \ "seriesRoundId").asOpt[String], numberOfBracketRounds, "B", seriesId, (jsonBracket \ "roundNr").asOpt[Int].getOrElse(0))

  def parseRobinFromJson(jsonRobin: JsValue): Option[RobinRound] = for {
    numberOfBracketRounds <- (jsonRobin \ "numberOfRobinGroups").asOpt[Int]
    seriesId <- (jsonRobin \ "seriesId").asOpt[String]
  } yield RobinRound((jsonRobin \ "seriesRoundId").asOpt[String], numberOfBracketRounds, "R", seriesId, (jsonRobin \ "roundNr").asOpt[Int].getOrElse(0))

  def getRoundsOfSeries(seriesId: String) = Action.async {
    seriesRoundDb.getRoundsListOfSeries(seriesId).map(roundList => Ok(Json.toJson(roundList)))
  }

  def drawSeries(seriesId: String, currentRoundNr: Int) = Action.async {
    // deleteRound


    seriesRoundDb.getSeriesRound(seriesId, currentRoundNr).flatMap {
      case Some(seriesRound) =>
        seriesRound match {
          case r: RobinRound =>
            robinDb.deleteRobinsFromSeriesRound(r.seriesRoundId.get).flatMap { deletedRows =>
              seriesPlayerDb.getPlayersOfSeries(r.seriesId).flatMap { seriesPlayers =>
                Logger.info(seriesPlayers.mkString(" "))
                val robinList = Future.sequence(Draw.splitPlayersInGroups(seriesPlayers, r.numberOfRobins).map { playerList => robinDb.createRobin(r.seriesRoundId.get, playerList, 2, 21) })
                robinList.map(list => Ok(Json.toJson(list)))
              }
            }
          case bracket: SiteBracketRound => Future(Ok)
        }
      case None => Future(BadRequest)
    }
  }

  def showRobinRound(roundId: String) = Action.async{
    seriesRoundDb.getSeriesRound(roundId).flatMap {
      case Some(seriesRound) =>
        robinDb.getRobinsOfRound(roundId).map { robinsList =>
          println(robinsList.mkString("\n"))
          Ok{
          Json.toJson {
            robinsList.map { robinRound => Json.toJson(robinRound)
            }
          }
        }
        }
      case _ => Future(BadRequest)

    }
  }
}








