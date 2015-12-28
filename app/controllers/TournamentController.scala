package controllers

import javax.inject.Inject

import db.slick.{SeriesDb, TournamentDb}
import models.Tournament
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller}
import utils.JsonUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TournamentController @Inject()(tournamentDb: TournamentDb, seriesDb: SeriesDb) extends Controller with TournamentWrites {
  val logger = LoggerFactory.getLogger(classOf[TournamentController])

  def getTournament(tournamentId: Int) = Action.async {

    tournamentDb.getTournament(tournamentId).flatMap { tournamentOpt =>
      seriesDb.getSeriesListOfTournament(tournamentId).map { seriesList =>
        val tournamentJson = Json.toJson(Map(
          "tournament" -> Json.toJson(tournamentOpt.map(Json.toJson(_))),
          "series" -> Json.toJson(seriesList.map(Json.toJson(_)))
        ))
        Ok(tournamentJson)
      }
    }
  }


  def getAllTournaments = Action.async {
    tournamentDb.getAllTournaments.map(tournamentList => Ok(Json.toJson(tournamentList.map(Json.toJson(_)))))
  }

  def insertTournament = Action.async { request =>
    request.body.asJson.map { body =>
      val tournamentOption = parseTournament(body)
      tournamentOption match {
        case Some(tournament) =>
          tournamentDb.insertTournament(tournament).map(tournamentId => Ok(Json.toJson(tournamentId)))

        case None => Future(BadRequest)
      }
    }.getOrElse(Future(BadRequest))
  }

  def updateTournament(tournamentId: Int) = Action.async { request =>
    val body = request.body.asJson.get
    val tournamentOption = parseTournament(body)
    tournamentOption match {
      case Some(tournament) =>
        tournamentDb.updateTournament(tournament).map(_ => Ok)
      case None => Future(BadRequest)
    }
  }

  def deleteTournament(tournamentId: Int) = Action.async {
    tournamentDb.deleteTournament(tournamentId).map(_ => Ok)
  }

  def parseTournament(jsonTournament: JsValue): Option[Tournament] = {
    logger.info(jsonTournament.toString())
    for {
      tournamentName <- (jsonTournament \ "tournamentName").asOpt[String]
      tournamentDate <- JsonUtils.parseJsonToLocalDate((jsonTournament \ "tournamentDate").getOrElse(Json.parse("{}")))
      maximumNumberOfSeriesEntries <- (jsonTournament \ "maximumNumberOfSeriesEntries").asOpt[Int]
      hasMultipleSeries <- (jsonTournament \ "hasMultipleSeries").asOpt[Boolean]
      showClub <- (jsonTournament \ "showClub").asOpt[Boolean]

    } yield Tournament((jsonTournament \ "tournamentId").asOpt[Int], tournamentName, tournamentDate, maximumNumberOfSeriesEntries, hasMultipleSeries, showClub)
  }
}



