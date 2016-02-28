package models

import scala.language.postfixOps
import slick.lifted.{Tag, TableQuery}
import slick.driver.MySQLDriver.api.{TableQuery => _, _}

case class TournamentSeries(
                             seriesId: Option[String],
                             seriesName: String,
                             seriesColor: String,
                             numberOfSetsToWin: Int,
                             setTargetScore: Int,
                             playingWithHandicaps: Boolean,
                             extraHandicapForRecs: Int,
                             showReferees: Boolean,
                             currentRoundNr: Int,
                             tournamentId: String)

case class SeriesWithRounds(seriesId: Option[String],
                            seriesName: String,
                            seriesColor: String,
                            numberOfSetsToWin: Int,
                            setTargetScore: Int,
                            playingWithHandicaps: Boolean,
                            extraHandicapForRecs: Int,
                            showReferees: Boolean,
                            tournamentId: String,
                            currentRoundNr: Int,
                            rounds: List[SeriesRound])

class SeriesTable(tag: Tag) extends Table[TournamentSeries](tag, "TOURNAMENT_SERIES") {

  def id = column[String]("SERIES_ID", O.PrimaryKey, O.Length(100))
  def name = column[String]("SERIES_NAME")
  def seriesColor = column[String]("PASSWORD")
  def numberOfSetsToWin = column[Int]("NUMBER_OF_SETS_TO_WIN")
  def setTargetScore = column[Int]("SET_TARGET_SCORE")
  def playingWithHandicaps = column[Boolean]("PLAYING_WITH_HANDICAPS")
  def extraHandicapForRecs = column[Int]("EXTRA_HANDICAP_FOR_RECS")
  def showReferees= column[Boolean]("SHOW_REFEREES")
  def currentRoundNr= column[Int]("CURRENT_ROUND_NR")
  def tournamentId = column[String]("TOURNAMENT_ID")

  def * = (id.?, name, seriesColor,numberOfSetsToWin, setTargetScore, playingWithHandicaps, extraHandicapForRecs, showReferees, currentRoundNr, tournamentId) <> ((TournamentSeries.apply _ ).tupled, TournamentSeries.unapply)
}