package db.slick

import com.google.inject.Inject
import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.JdbcProfile
import slick.jdbc.GetResult
import utils.DbUtils
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future


class MatchDb @Inject()(@NamedDatabase("default") protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  implicit val getResult: GetResult[SiteMatch] = GetResult(r => SiteMatch(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))

  import driver.api._


  private val siteMatches = TableQuery[SiteMatchTable]
  private val siteGames = TableQuery[SiteGameTable]
  private val robinMatches = TableQuery[RobinMatchTable]

  val schema = siteMatches.schema ++ siteGames.schema ++ robinMatches.schema
  db.run(DBIO.seq(schema.create))

  def getNextMatchId: String = DbUtils.generateId

  def createRobinMatches(robinId: String, playersList: List[RobinPlayer], numberOfSetsToWin: Int, setTargetScore: Int): Future[List[SiteMatchWithGames]] = {
    Future.sequence {
      playersList.init.zipWithIndex.flatMap{ playerWithIndex =>
        val playerA = playerWithIndex._1
        playersList.drop(playerWithIndex._2 + 1).map { playerB => {
          val relHandicap = playerA.rankValue - playerB.rankValue
          val isForB = relHandicap > 0
          createDbMatch(SiteMatch(Some(DbUtils.generateId), playerA.seriesPlayerId, playerB.seriesPlayerId, Math.abs(relHandicap), isForB, setTargetScore, numberOfSetsToWin)).flatMap{ siteMatch =>
          val matchToInsert =  RobinMatch(Some(DbUtils.generateId), robinId, siteMatch.matchId.get)
            db.run(robinMatches +=matchToInsert).flatMap{ _ =>
             db.run(siteGames.filter( _.matchId === matchToInsert.matchId).result).map{ sets =>
              SiteMatchWithGames(siteMatch.matchId, siteMatch.playerA, siteMatch.playerB, siteMatch.handicap, siteMatch.isHandicapForB, siteMatch.targetScore, siteMatch.numberOfSetsToWin, sets.toList)
             }
        }
        }
        }
      }
    }
  }
  }

  def createDbMatch(siteMatch: SiteMatch): Future[SiteMatch] = {
    val insertedMatchId =  DbUtils.generateId
      db.run(siteMatches += SiteMatch(Some(insertedMatchId), siteMatch.playerA, siteMatch.playerB, siteMatch.handicap, siteMatch.isHandicapForB, siteMatch.numberOfSetsToWin, siteMatch.targetScore))
        .flatMap { insertedMatchNr =>
          Future.sequence {
            (1 to (siteMatch.numberOfSetsToWin * 2 - 1)).toList
              .map{set => {
                val insertedSetId = DbUtils.generateId
                db.run(siteGames += SiteGame(Some(insertedSetId),insertedMatchId, 0, 0)) .map{ _ =>
                  val gameId= DbUtils.generateId
                  SiteGame(Some(gameId), insertedMatchId , 0, 0)}
              }
              }
          }.map( _ => siteMatch.copy(matchId= Some(insertedMatchId)))
        }
  }

  def deleteAll = {
    db.run(siteGames.delete)
    db.run(siteMatches.delete)
  }
}
