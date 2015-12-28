package models

case class TournamentSeries(
                             seriesId: Option[Int],
                             seriesName: String,
                             seriesColor: String,
                             numberOfSetsToWin: Int,
                             setTargetScore: Int,
                             playingWithHandicaps: Boolean,
                             extraHandicapForRecs: Int,
                             showReferees: Boolean,
                             tournamentId: Int)

case class SeriesWithRounds(seriesId: Option[Int],
                            seriesName: String,
                            seriesColor: String,
                            numberOfSetsToWin: Int,
                            setTargetScore: Int,
                            playingWithHandicaps: Boolean,
                            extraHandicapForRecs: Int,
                            showReferees: Boolean,
                            tournamentId: Int,
                            rounds: List[SeriesRound])
