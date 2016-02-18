package db.slick

import com.google.inject.Inject
import models.{RobinPlayerTable, RoundRobinTable, RobinPlayer, RoundRobinGroup}
import org.slf4j.LoggerFactory
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.JdbcProfile
import slick.jdbc.GetResult
import utils.DbUtils
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RobinDb @Inject()(@NamedDatabase("default") protected val dbConfigProvider: DatabaseConfigProvider, matchDb: MatchDb) extends HasDatabaseConfigProvider[JdbcProfile]{

  implicit val getResult: GetResult[RoundRobinGroup] = GetResult(r => RoundRobinGroup(r.<<, r.<<))
  import driver.api._

  val roundRobinCollection = TableQuery[RoundRobinTable]
  private val robinPlayerCollection = TableQuery[RobinPlayerTable]

  // create the db
  val schema = roundRobinCollection.schema ++ robinPlayerCollection.schema
  db.run(DBIO.seq(schema.create))
  val logger = LoggerFactory.getLogger(this.getClass)

  def deleteRobinsFromSeriesRound(seriesRoundId: String): Future[Int] = {
    db.run(roundRobinCollection.filter(_.seriesRoundId === seriesRoundId).result).flatMap { robins =>
      Future.sequence(robins.map(robin => db.run(robinPlayerCollection.filter(_.roundRobinGroupId === robin.robinId.get).delete)))
    }.flatMap( _ => db.run(roundRobinCollection.filter(_.seriesRoundId === seriesRoundId).delete))
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

  // clearing to redraw! Matches should be cleared by matchDb
  def clearRobin(seriesRoundId: String): Unit ={
    db.run(roundRobinCollection.filter( _.seriesRoundId === seriesRoundId).delete)
  }

  def createRobinPlayer(robinPlayer: RobinPlayer): Future[RobinPlayer] ={
    val playerToInsert = robinPlayer.copy(robinPlayerId=Some(DbUtils.generateId))
    db.run(robinPlayerCollection += playerToInsert).map { _ => playerToInsert}
  }

}
