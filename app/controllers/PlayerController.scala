
package controllers

import javax.inject.Inject
import db.slick.PlayerDb
import models.{Player, Rank, Ranks}
import models.Player._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller}
import utils.RankConverter
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future


class PlayerController @Inject()(implicit val playerDb: PlayerDb) extends Controller {
  //implicit val rankWrites = Json.writes[Rank]
  //implicit val playerWithIdWrites = Json.writes[Player]



  def getAllPlayers = Action.async {
    playerDb.getAllPlayers.map(players => Ok(Json.toJson(players.map(Json.toJson(_)))))
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

  def updatePlayer = Action.async { request =>
    request.body.asJson.flatMap { body =>
      parsePlayerFromJson(body).map {
        player => playerDb.updatePlayer(player).flatMap { _ =>
          playerDb.getPlayer(player.playerId.get).map {
            case Some(updatedPlayer) => Ok(Json.toJson(updatedPlayer))
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



