package db.slick

import com.google.inject.Inject
import models._
import org.slf4j.LoggerFactory
import play.api.Logger
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.JdbcProfile
import slick.jdbc.GetResult
import utils.DbUtils
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RobinDb @Inject()(@NamedDatabase("default") protected val dbConfigProvider: DatabaseConfigProvider, matchDb: MatchDb, seriesPlayerDb: SeriesPlayersDb, playerDb: PlayerDb) extends HasDatabaseConfigProvider[JdbcProfile]{

  implicit val getResult: GetResult[RoundRobinGroup] = GetResult(r => RoundRobinGroup(r.<<, r.<<))
  import driver.api._

  val roundRobinCollection = TableQuery[RoundRobinTable]
  private val robinPlayerCollection = TableQuery[RobinPlayerTable]

  // create the db
  val schema = roundRobinCollection.schema ++ robinPlayerCollection.schema
  db.run(DBIO.seq(schema.create))
  val logger = LoggerFactory.getLogger(this.getClass)

  def deleteRobinsFromSeriesRound(seriesRoundId: String): Future[Int] = {
    db.run(roundRobinCollection.filter(_.seriesRoundId === seriesRoundId).result).flatMap(robins =>
      Future.sequence(robins.map{ robin =>
        matchDb.deleteMatchesFromRobinRound(robin).flatMap(deletedMatches => db.run(robinPlayerCollection.filter(_.roundRobinGroupId === robin.robinId.get).delete))
      }).flatMap(_ => db.run(roundRobinCollection.filter(_.seriesRoundId === seriesRoundId).delete)))
  }


  def createRobin(seriesRoundId: String, robinPlayers: List[RobinPlayer], numberOfSetsToWin: Int, setTargetScore: Int): Future[RoundRobinGroup] = {
    val robinGroupToInsert = RoundRobinGroup(Some(DbUtils.generateId), seriesRoundId)
    db.run(roundRobinCollection += robinGroupToInsert).flatMap { _ =>
      Future.sequence {robinPlayers.map(player =>
        createRobinPlayer(player.copy(robinGroupId = robinGroupToInsert.robinId)))}
        .flatMap { playersList =>
        matchDb.createRobinMatches(robinGroupToInsert.robinId.get, playersList, numberOfSetsToWin, setTargetScore).map { robinMatches =>
          robinGroupToInsert
        }
      }
    }
  }

  def deletePlayersFromRobin() = {

  }

  def createRobinPlayer(robinPlayer: RobinPlayer): Future[RobinPlayer] ={
    val playerToInsert = robinPlayer.copy(robinPlayerId=Some(DbUtils.generateId))
    db.run(robinPlayerCollection += playerToInsert).map { _ => playerToInsert}
  }

  def getRobinsOfRound(seriesRoundId: String): Future[List[RobinGroupWithMatchesAndPlayers]] = {
    db.run(roundRobinCollection.filter(_.seriesRoundId === seriesRoundId).result).flatMap { robins =>
      Future.sequence {
        robins.toList.map { robin =>
          db.run(robinPlayerCollection.filter(_.roundRobinGroupId === robin.robinId.get).result).flatMap { robinPlayers =>
            Future.sequence {robinPlayers.map { robinPlayer => getRobinWithName(robinPlayer)}}.flatMap { namedPlayers =>
              matchDb.getRobinMatches(robin.robinId.get).map {
                matches => RobinGroupWithMatchesAndPlayers(robin.robinId.get, robin.seriesRoundId, namedPlayers.flatten.toList, matches)
              }
            }
          }
        }
      }
    }
  }

  def getRobinWithName(robinPlayer: RobinPlayer): Future[Option[RobinPlayerWithName]] = {
    seriesPlayerDb.getSeriesPlayer(robinPlayer.seriesPlayerId).flatMap {
      case Some(seriesPlayer) => playerDb.getPlayer(seriesPlayer.playerId).map {
        case Some(player) => Some(RobinPlayerWithName(
          robinPlayer.robinPlayerId,
          robinPlayer.seriesPlayerId,
          player.firstname,
          player.lastname,
          robinPlayer.rankValue,
          robinPlayer.robinNr,
          robinPlayer.wonMatches,
          robinPlayer.lostMatches,
          robinPlayer.wonSets,
          robinPlayer.lostSets,
          robinPlayer.wonPoints,
          robinPlayer.lostPoints))
        case _ => None
      }
      case None => Future(None)
    }
  }
}
