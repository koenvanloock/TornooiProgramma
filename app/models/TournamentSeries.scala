package models

case class TournamentSeries(
                             seriesId: Option[String],
                             seriesName: String,
                             seriesColor: String,
                             numberOfSetsToWin: Int,
                             setTargetScore: Int,
                             playingWithHandicaps: Boolean,
                             extraHandicapForRecs: Int,
                             showReferees: Boolean,
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
                            rounds: List[SeriesRound])
