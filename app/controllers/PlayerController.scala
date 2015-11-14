package controllers

import javax.inject.Inject

import db.PlayerDb
import models.{Rank, Ranks}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.ReactiveMongoApi
import utils.RankConverter
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future


class PlayerController extends Controller {
  implicit val rankWrites = Json.writes[Rank]
  implicit val playerWithIdWrites = Json.writes[Player]
  val playerDb = new PlayerDb


  def getAllPlayers = Action.async {
    playerDb.getAllPlayers.map(players => Ok(Json.toJson(players)))
  }

  def insertPlayer() = Action.async { request =>
    request.body.asJson.map {
      body => parsePlayerFromJson(body) match {
        case Some(playerToInsert) => playerDb.insertPlayer(playerToInsert).map {
          player => Created(Json.toJson(player)) }
        case None => Future(BadRequest)
      }
    }.getOrElse(Future(BadRequest))
  }

  def updatePlayer(playerId: String) = Action.async { request =>
    request.body.asJson.flatMap { body =>
      parsePlayerFromJson(body).map {
        player => playerDb.updatePlayer(player, playerId).flatMap { _ =>
          playerDb.getPlayer(playerId).map {
            case Some(updatedPlayer) => Ok(Json.toJson(updatedPlayer)(playerWithIdWrites))
            case None => BadRequest
          }
        }
      }
    }.getOrElse(Future(BadRequest))
  }


  def deletePlayer(playerId: String) = Action.async { request =>
    playerDb.deletePlayer(playerId).map(_ => Ok)

  }

  def getRanks = Action {
    Ok(Json.toJson(Ranks.RankList.map(Json.toJson(_))))
  }

  def parsePlayerFromJson(playerJson: JsValue): Option[Player] = for (
    first <- (playerJson \ "firstname").asOpt[String];
    last <- (playerJson \ "lastname").asOpt[String];
    rank <- (playerJson \ "rank" \ "value").asOpt[Int].map(RankConverter.getRankOfInt)
  ) yield Player((playerJson \ "playerId").asOpt[String],first, last, rank)

}

case class Player(playerId: Option[String], firstname: String, lastname: String, rank: Rank)
