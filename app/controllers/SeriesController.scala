package controllers


import db.SeriesDb
import models.Rank
import org.slf4j.LoggerFactory
import play.api.Logger
import play.modules.reactivemongo.ReactiveMongoApi
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future


class SeriesController extends Controller with TournamentWrites{
  val logger = LoggerFactory.getLogger(classOf[SeriesController])
  val seriesDb = new SeriesDb
  
  def parseSeriesFromJson(seriesJson: JsValue): Option[TournamentSeries] = for(
    name <- (seriesJson \ "seriesName").asOpt[String];
    seriesColor <- (seriesJson \ "seriesColor").asOpt[String];
    setTargetScore <- (seriesJson \ "setTargetScore").asOpt[Int];
    numberOfSetsToWin <- (seriesJson \ "numberOfSetsToWin").asOpt[Int];
    playingWithHandicaps <- (seriesJson \ "playingWithHandicaps").asOpt[Boolean];
    extraHandicapForRecs <- (seriesJson \ "extraHandicapForRecs").asOpt[Int];
    showReferees <- (seriesJson \ "showReferees").asOpt[Boolean];
    tournamentId <- (seriesJson \ "tournamentId").asOpt[Int])
  yield TournamentSeries("0", name, seriesColor, numberOfSetsToWin,setTargetScore, playingWithHandicaps, extraHandicapForRecs, showReferees,tournamentId,List(),List())

  def createSeries = Action.async{
    request =>
      val json = request.body.asJson.get
      Logger.info(json.toString())
      parseSeriesFromJson(json).map{ series =>
        logger.info(series.toString)
        seriesDb.insertSeries(series).map(_ => Created)

      }.getOrElse(Future(BadRequest))
  }

  def updateSeries(seriesId: String) = Action{
    request =>
      val json = request.body.asJson.get
      parseSeriesFromJson(json).map{ series =>
        seriesDb.updateSeries(series, seriesId)
        NoContent
      }.getOrElse(BadRequest)
  }

  def getSeriesOfTournament(tournamentId: String) = Action.async{
    val seriesFutList = seriesDb.getSeriesListOfTournament(tournamentId)
    seriesFutList.map{ seriesList => Ok(Json.toJson(seriesList.map(Json.toJson(_))))}
  }
}
case class SeriesPlayer(seriesPlayerId: Long, firstname: String, lastname: String, club: String, rank: Rank, wonMatches: Int, lostMatches: Int, wonSets: Int, lostSets: Int, wonPoints: Int, lostPoints: Int)
case class TournamentSeries(
                             seriesId: String,
                             seriesName: String,
                             seriesColor: String,
                             numberOfSetsToWin: Int,
                             setTargetScore: Int,
                             playingWithHandicaps: Boolean,
                             extraHandicapForRecs: Int,
                             showReferees: Boolean,
                             tournamentId: Int,
                             seriesPlayers: List[SeriesPlayer],
                             seriesRounds: List[SeriesRound])