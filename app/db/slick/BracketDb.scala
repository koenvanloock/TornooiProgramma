package db.slick

import com.google.inject.Inject
import models._
import models.BracketRound.BracketRoundTable
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.JdbcProfile
import utils.DbUtils
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class BracketDb @Inject()(@NamedDatabase("default") protected val dbConfigProvider: DatabaseConfigProvider, matchDb: MatchDb, seriesRoundDb: SeriesRoundDb, seriesDb: SeriesDb) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val BracketRoundCollection = TableQuery[BracketRoundTable]
  val BracketMatchCollection = TableQuery[BracketMatchTable]

  def drawBracket(seriesRoundId: String, seriesPlayers: List[SeriesPlayer]): Future[List[BracketRound]] = {
    seriesRoundDb.getSeriesRound(seriesRoundId).flatMap {
      case Some(bracketRound: SiteBracketRound) =>
        seriesDb.getSeries(bracketRound.seriesId).flatMap {
          case Some(series) =>
            createRounds(seriesRoundId, seriesPlayers, bracketRound.numberOfBracketRounds, series.setTargetScore, series.numberOfSetsToWin)
          case _ => Future(List())
        }
      case _ => Future(List())
    }
  }

  def createRounds(seriesRoundId: String, seriesPlayers: List[SeriesPlayer], roundNrToCreate: Int, setTargetScore: Int, numberOfSetsToWin: Int): Future[List[BracketRound]] = {
    val roundToCreate = BracketRound(Some(DbUtils.generateId), seriesRoundId, roundNrToCreate)
    db.run(BracketRoundCollection += roundToCreate).flatMap(createdRound =>
      createRounds(seriesRoundId, seriesPlayers, roundNrToCreate - 1, setTargetScore, numberOfSetsToWin)).map(roundList =>
      roundToCreate :: roundList)
  }

  def createBracketMatch(bracketRoundId: String, playerA: SeriesPlayer, playerB: SeriesPlayer, targetScore: Int, numberOfSetsToWin: Int): Future[Option[BracketMatch]] = {
    val handicap = Math.abs(playerA.rank.value - playerB.rank.value)
    val isForB = playerA.rank.value - playerB.rank.value > 0
    matchDb.createDbMatch(SiteMatch(None, playerA.playerId, playerB.playerId, handicap, isForB, targetScore, numberOfSetsToWin)).flatMap{
      siteMatch =>
        val matchToInsert = BracketMatch(Some(DbUtils.generateId), bracketRoundId, siteMatch.matchId.get)
        db.run(BracketMatchCollection += matchToInsert).map( _ => Some(matchToInsert))
    }
  }

  def createRoundMatches(bracketRoundId: String, roundNr: Int, seriesPlayers: List[SeriesPlayer], setTargetScore: Int, numberOfSetsToWin: Int) = {

    roundNr match {
      case 5 => Future(List())
      case 4 => if (seriesPlayers.length > 15) {
        createBracketMatch(bracketRoundId, seriesPlayers.head, seriesPlayers.drop(15).head, setTargetScore, numberOfSetsToWin)
        createBracketMatch(bracketRoundId, seriesPlayers.drop(8).head, seriesPlayers.drop(7).head, setTargetScore, numberOfSetsToWin)
        createBracketMatch(bracketRoundId, seriesPlayers.drop(4).head, seriesPlayers.drop(11).head, setTargetScore, numberOfSetsToWin)
        createBracketMatch(bracketRoundId, seriesPlayers.drop(12).head, seriesPlayers.drop(3).head, setTargetScore, numberOfSetsToWin)

        createBracketMatch(bracketRoundId, seriesPlayers.drop(2).head, seriesPlayers.drop(13).head, setTargetScore, numberOfSetsToWin)
        createBracketMatch(bracketRoundId, seriesPlayers.drop(10).head, seriesPlayers.drop(5).head, setTargetScore, numberOfSetsToWin)
        createBracketMatch(bracketRoundId, seriesPlayers.drop(6).head, seriesPlayers.drop(9).head, setTargetScore, numberOfSetsToWin)
        createBracketMatch(bracketRoundId, seriesPlayers.drop(14).head, seriesPlayers.drop(1).head, setTargetScore, numberOfSetsToWin)

      } else {
        Future(List())
      }
      case 3 => if (seriesPlayers.length > 7) {
        createBracketMatch(bracketRoundId, seriesPlayers.head, seriesPlayers.drop(7).head, setTargetScore, numberOfSetsToWin)
        createBracketMatch(bracketRoundId, seriesPlayers.drop(4).head, seriesPlayers.drop(3).head, setTargetScore, numberOfSetsToWin)
        createBracketMatch(bracketRoundId, seriesPlayers.drop(2).head, seriesPlayers.drop(5).head, setTargetScore, numberOfSetsToWin)
        createBracketMatch(bracketRoundId, seriesPlayers.drop(6).head, seriesPlayers.drop(1).head, setTargetScore, numberOfSetsToWin)
      } else {
        Future(List())
      }
      case 2 => if (seriesPlayers.length > 3) {
        createBracketMatch(bracketRoundId, seriesPlayers.head, seriesPlayers.drop(3).head, setTargetScore, numberOfSetsToWin)
        createBracketMatch(bracketRoundId, seriesPlayers.drop(2).head, seriesPlayers.drop(1).head, setTargetScore, numberOfSetsToWin)
      } else {
        Future(List())
      }
      case _ => if (seriesPlayers.length > 1) {
        createBracketMatch(bracketRoundId, seriesPlayers.head, seriesPlayers.drop(1).head, setTargetScore, numberOfSetsToWin)
      } else {
        Future(List())
      }
    }
  }

  val schema = BracketRoundCollection.schema
  db.run(DBIO.seq(schema.create))


  def createBracketRound(bracketRound: BracketRound) = {
    db.run(BracketRoundCollection += bracketRound.copy(id = Some(DbUtils.generateId))).map { insertedRound => insertedRound }
  }

  def deleteBracketRound(bracketRoundId: String) = {
    db.run(BracketRoundCollection.filter(_.id === bracketRoundId).delete)
  }

}
