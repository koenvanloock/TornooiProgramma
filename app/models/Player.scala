package models

import play.api.libs.json.Json

case class Player(playerId: Option[Int], firstname: String, lastname: String, rank: Rank)
object Player{
  implicit val rankFormat = Json.format[Rank]
  implicit val playerFormat = Json.format[Player]
}