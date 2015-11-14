package controllers

import java.time.LocalDate
import javax.inject.Inject

import play.modules.reactivemongo.ReactiveMongoApi

import scala.concurrent.ExecutionContext.Implicits.global
import db.{SeriesDb, TournamentDb}
import models.Tournament
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller}
import utils.JsonUtils

import scala.concurrent.Future

class TournamentController extends Controller with TournamentWrites{
  val logger= LoggerFactory.getLogger(classOf[TournamentController])

  val tournamentDb = new TournamentDb
  val seriesDb = new SeriesDb
  def getTournament(tournamentId: String) = Action.async{
    for{
      tournamentOpt <- tournamentDb.getTournament(tournamentId)
      seriesList <- seriesDb.getSeriesListOfTournament(tournamentId)
    }yield {

      val tournamentJson = Json.toJson(Map(
        "tournament" -> Json.toJson(tournamentOpt.map(Json.toJson(_))),
        "series" -> Json.toJson(seriesList.map(Json.toJson(_)))
      ))
      Ok(tournamentJson)
    }
  }


  def getAllTournaments = Action.async{
    tournamentDb.getTournaments.map( tournamentList => Ok(Json.toJson( tournamentList.map(Json.toJson(_)))))
  }

  def insertTournament = Action.async { request =>
    request.body.asJson.map{ body =>
    val tournamentOption = parseTournament(body)
    tournamentOption match {
      case Some(tournament) =>
                    tournamentDb.insertTournament(tournament).map( tournamentId => Ok(Json.toJson(tournamentId)))

      case None =>  Future(BadRequest)
    }
    }.getOrElse(Future(BadRequest))
  }

  def updateTournament(tournamentId: String) = Action.async { request =>
    val body = request.body.asJson.get
    val tournamentOption = parseTournament(body)
    tournamentOption match {
      case Some(tournament) =>
        tournamentDb.updateTournament(tournament).map(_ => Ok)
      case None =>  Future(BadRequest)
    }
  }

  def deleteTournament(tournamentId: String) = Action.async{
    tournamentDb.deleteTournament(tournamentId).map(_ => Ok)
  }

  def parseTournament(jsonTournament: JsValue): Option[Tournament] ={
    logger.info(jsonTournament.toString())
    for{
      tournamentName <- (jsonTournament \ "tournamentName").asOpt[String]
      tournamentDate <- JsonUtils.parseJsonToLocalDate((jsonTournament \ "tournamentDate").getOrElse(Json.parse("{}")))
      maximumNumberOfSeriesEntries <- (jsonTournament \ "maximumNumberOfSeriesEntries").asOpt[Int]
      hasMultipleSeries <- (jsonTournament \ "hasMultipleSeries").asOpt[Boolean]
      showClub <- (jsonTournament \ "showClub").asOpt[Boolean]

    } yield Tournament((jsonTournament \ "tournamentId").asOpt[String], tournamentName, tournamentDate, maximumNumberOfSeriesEntries, hasMultipleSeries, showClub)
  }

}



