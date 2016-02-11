package models

import play.api.libs.json.Json

case class TournamentPlayer(tournamentPlayerId: Option[String], firstname: String, lastname: String, rank: Rank, tournamentId: Int)

object TournamentPlayer{
  implicit val rankFormat = Json.format[Rank]
  implicit val tournamentPlayerFormat = Json.format[TournamentPlayer]
}