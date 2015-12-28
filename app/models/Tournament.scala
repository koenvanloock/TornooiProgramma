package models

import java.time.LocalDate

case class Tournament(tournamentId: Option[Int], tournamentName: String, tournamentDate: LocalDate, maximumNumberOfSeriesEntries: Int, hasMultipleSeries: Boolean, showClub: Boolean)

