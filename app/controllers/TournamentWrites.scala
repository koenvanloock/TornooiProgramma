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

  implicit val localDateReads: Reads[LocalDate] = (
      (__ \ "year").read[Int] and
        (__ \ "month").read[Int] and
        (__ \ "day").read[Int]
      )(LocalDate.of(_,_,_))

  implicit val seriesPlayerFormat = Json.format[SeriesPlayer]
  implicit val robinPlayerFormat = Json.format[RobinPlayer]

  implicit val gameWrites = Json.format[SiteGame]

  implicit val matchWrites = new Writes[SiteMatch] {
    def writes(pingpongMatch: SiteMatch) = Json.obj(
      "playerANr" -> Json.toJson(pingpongMatch.playerA),
      "playerBNr" -> Json.toJson( pingpongMatch.playerB),
      "handicap" -> Json.toJson( pingpongMatch.handicap),
      "isHandicapForB" -> Json.toJson( pingpongMatch.isHandicapForB),
      "numberOfSetsForA" -> Json.toJson( pingpongMatch.numberOfSetsForA),
      "numberOfSetsForB" -> Json.toJson( pingpongMatch.numberOfSetsForB),
      "sets" -> Json.toJson(pingpongMatch.sets.map(Json.toJson(_)))
    )
  }

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

  implicit val seriesRoundWrites = new Writes[SeriesRound] {
    def writes(seriesRound: SeriesRound) = {
      seriesRound match {
        case robin: RobinRound => Json.toJson(robin)
        case bracket: SiteBracketRound => Json.toJson(bracket)
      }
    }
  }

  implicit val seriesFormat = Json.format[TournamentSeries]

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


  implicit val tournamentFormat = Json.format[Tournament]
}
