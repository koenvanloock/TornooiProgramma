package controllers

import java.time.LocalDate

import julienrf.variants.Variants
import models._
import play.api.libs.functional.syntax._
import play.api.libs.json._


trait TournamentWrites {

  implicit val rankFormat = Json.format[Rank]

  implicit val LocalDateWrites = new Writes[LocalDate] {
    def writes(date: LocalDate) = Json.obj(
      "day" -> date.getDayOfMonth,
      "month" -> date.getMonthValue,
      "year" -> date.getYear
    )
  }

  implicit val seriesPlayerFormat = Json.format[SeriesPlayer]
  implicit val robinPlayerFormat = Json.format[RobinPlayer]



  /*implicit val matchWrites = new Writes[SiteMatch] {
    def writes(pingpongMatch: SiteMatch) = Json.obj(
      "playerANr" -> Json.toJson(pingpongMatch.playerA),
      "playerBNr" -> Json.toJson( pingpongMatch.playerB),
      "handicap" -> Json.toJson( pingpongMatch.handicap),
      "isHandicapForB" -> Json.toJson( pingpongMatch.isHandicapForB),
      "numberOfSetsForA" -> Json.toJson( pingpongMatch.numberOfSetsForA),
      "numberOfSetsForB" -> Json.toJson( pingpongMatch.numberOfSetsForB),
      "sets" -> Json.toJson(pingpongMatch.sets.map(Json.toJson(_)))
    )
  }*/


 implicit val gameFormat = Json.format[SiteGame]

  implicit val siteMatchWrites: Writes[SiteMatch] = (
    (__ \ "matchId").write[String] and
      (__ \ "sets").write[List[SiteGame]](Writes.list[SiteGame]) and
      (__ \ "playerA").write[String] and
      (__ \ "playerB").write[String] and
      (__ \ "handicap").write[Int] and
      (__ \ "isHandicapForB").write[Boolean] and
      (__ \ "targetScore").write[Int] and
      (__ \ "numberOfSetsToWin").write[Int]
    )(unlift(SiteMatch.unapply))


  implicit val siteMatchReads: Reads[SiteMatch] = (
    (__ \ "matchId").read[String] and
      (__ \ "sets").read[List[SiteGame]](Reads.list[SiteGame]) and
      (__ \ "playerA").read[String] and
      (__ \ "playerB").read[String] and
      (__ \ "handicap").read[Int] and
      (__ \ "isHandicapForB").read[Boolean] and
      (__ \ "targetScore").read[Int] and
      (__ \ "numberOfSetsToWin").read[Int]
    )(SiteMatch.apply _)



  implicit val roundRobinGroupFormat = Json.format[RoundRobinGroup]
  implicit val robinRoundFormat = Json.format[RobinRound]

  implicit val bracketFormat = Json.format[SiteBracketRound]




  def parseBracketFromJson(jsonBracket: JsValue): Option[SiteBracketRound] = for{
    numberOfBracketRounds <- (jsonBracket \ "numberOfBracketRounds").asOpt[Int]
  }yield SiteBracketRound((jsonBracket \ "seriesRoundId").asOpt[Int], numberOfBracketRounds, "B",0) //still have to get roundNr

  def parseRobinFromJson(jsonBracket: JsValue): Option[RobinRound] = for{
    numberOfBracketRounds <- (jsonBracket \ "numberOfRobinGroups").asOpt[Int]
  }yield RobinRound((jsonBracket \ "seriesRoundId").asOpt[Int], numberOfBracketRounds, "R",0) //still have to get roundNr


  implicit val seriesRoundFormat: Format[SeriesRound] = Variants.format[SeriesRound]

  implicit val seriesFormat = Json.format[TournamentSeries]
/*
  implicit val tournamentWrites = new Writes[Tournament] {

    def writes(tournament: Tournament) = Json.obj(
      "tournamentId" -> Json.toJson(tournament.tournamentId),
      "tournamentName" -> Json.toJson(tournament.tournamentName),
      "tournamentDate" -> Json.toJson(tournament.tournamentDate),
      "maximumNumberOfSeriesEntries" -> Json.toJson(tournament.maximumNumberOfSeriesEntries),
      "hasMultipleSeries" -> Json.toJson(tournament.hasMultipleSeries),
      "showClub" -> Json.toJson(tournament.showClub)
    )
  }
*/

  implicit val tournamentFormat = Json.format[Tournament]
}
