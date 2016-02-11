package controllers

import com.google.inject.Inject
import db.slick.{PlayerDb, SeriesDb, SeriesPlayersDb}
import models.{Player, SeriesPlayer}
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller}
import utils.RankConverter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SeriesPlayerController @Inject()(seriesPlayerDb: SeriesPlayersDb, playerDb: PlayerDb, seriesDb: SeriesDb) extends Controller with TournamentWrites {

  def createSeriesPlayer(tournamentId: String) = Action.async { request =>
    request.body.asJson.map { json =>
      dropCreateSeriesPlayersOfTournament(json, tournamentId).map(jsonList => Ok(Json.toJson(jsonList)))
    }.getOrElse(Future(BadRequest))

  }

  def dropCreateSeriesPlayersOfTournament(json: JsValue, tournamentId: String) = {

    deleteSeriesSubscriptions(tournamentId, json).flatMap { deletes =>

      Future.sequence {
        json.as[List[JsValue]].flatMap { seriesPlayerJson =>
          parseSeriesPlayer(seriesPlayerJson).map { seriesPlayer =>
            seriesPlayerDb.getSeriesSubscriptionOfPlayer(seriesPlayer.seriesId, seriesPlayer.playerId).flatMap {
              case None =>
                seriesPlayerDb.create(seriesPlayer).map { createdSeriesPlayer => Json.toJson(createdSeriesPlayer) }
              case Some(foundSeriesPlayer) =>
                Future(Json.toJson(foundSeriesPlayer))
            }
          }
        }
      }
    }
  }

  def deleteSeriesSubscriptions(tournamentId: String, json: JsValue) = {
    Logger.info("delete")
    json.as[List[JsValue]].headOption.flatMap(parseSeriesPlayer).map { player =>
      Logger.info("Een player" + player)
      seriesDb.getSeriesListOfTournament(tournamentId).flatMap { seriesList =>
        Logger.info("serieslijst:" + seriesList)
        Future.sequence(seriesPlayerDb.deleteSubscriptions(seriesList.map(_.seriesId.get), player.playerId))
      }
    }.getOrElse(Future(0))
  }

  def getPlayersOfSeries(seriesId: String) = Action.async {

    val players: Future[List[Player]] = seriesPlayerDb.getPlayersOfSeries(seriesId).flatMap(seriesPlayerList => Future.sequence(seriesPlayerList.map(seriesPlayer => playerDb.getPlayer(seriesPlayer.playerId)))).map(_.flatten)
    players.map(playersList => Ok(Json.toJson(playersList.map(Json.toJson(_)))))
  }

  def parseSeriesPlayer(jsValue: JsValue): Option[SeriesPlayer] = {
    for {
      playerId <- (jsValue \ "playerId").asOpt[String]
      seriesId <- (jsValue \ "seriesId").asOpt[String]
      rank <- (jsValue \ "rank").asOpt[Int].map(RankConverter.getRankOfInt)
    } yield {
      SeriesPlayer(
        seriesPlayerId = (jsValue \ "seriesPlayerId").asOpt[String],
        playerId = playerId,
        rank = rank,
        seriesId = seriesId)
    }
  }
}
