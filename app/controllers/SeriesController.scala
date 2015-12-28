package controllers


import javax.inject.Inject

import db.slick.SeriesDb
import models.{TournamentSeries, SeriesPlayer, Rank}
import org.slf4j.LoggerFactory
import play.api.Logger
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future


class SeriesController @Inject()(val seriesDb: SeriesDb) extends Controller with TournamentWrites{
  val logger = LoggerFactory.getLogger(classOf[SeriesController])
  
  def parseSeriesFromJson(seriesJson: JsValue): Option[TournamentSeries] = for(
    name <- (seriesJson \ "seriesName").asOpt[String];
    seriesColor <- (seriesJson \ "seriesColor").asOpt[String];
    setTargetScore <- (seriesJson \ "setTargetScore").asOpt[Int];
    numberOfSetsToWin <- (seriesJson \ "numberOfSetsToWin").asOpt[Int];
    playingWithHandicaps <- (seriesJson \ "playingWithHandicaps").asOpt[Boolean];
    extraHandicapForRecs <- (seriesJson \ "extraHandicapForRecs").asOpt[Int];
    showReferees <- (seriesJson \ "showReferees").asOpt[Boolean];
    tournamentId <- (seriesJson \ "tournamentId").asOpt[Int])
  yield TournamentSeries(None, name, seriesColor, numberOfSetsToWin,setTargetScore, playingWithHandicaps, extraHandicapForRecs, showReferees,tournamentId)

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
        seriesDb.updateSeries(series)
        NoContent
      }.getOrElse(BadRequest)
  }

  def getSeriesOfTournament(tournamentId: Int) = Action.async{
    val seriesFutList = seriesDb.getSeriesListOfTournament(tournamentId)
    seriesFutList.map{ seriesList => Ok(Json.toJson(seriesList.map(Json.toJson(_))))}
  }
}

