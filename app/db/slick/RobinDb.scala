package db.slick

import com.google.inject.Inject
import models.{RobinPlayer, RoundRobinGroup}
import org.slf4j.LoggerFactory
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.JdbcProfile
import slick.jdbc.GetResult
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class RoundRobinGroupDb(roundRobinGroupId: Option[Int], seriesRoundId: Int)

class RobinDb @Inject()(@NamedDatabase("default") protected val dbConfigProvider: DatabaseConfigProvider, matchDb: MatchDb) extends HasDatabaseConfigProvider[JdbcProfile]{

  implicit val getResult: GetResult[RoundRobinGroupDb] = GetResult(r => RoundRobinGroupDb(r.<<, r.<<))
  import driver.api._

  private val roundRobinCollection = TableQuery[RoundRobinTable]
  private val robinPlayerCollection = TableQuery[RobinPlayerTable]

  // create the db
  val schema = roundRobinCollection.schema ++ robinPlayerCollection.schema
  db.run(DBIO.seq(schema.create))
  val logger = LoggerFactory.getLogger(this.getClass)

  def createRobin(seriesRoundId: Int, robinPlayers: List[RobinPlayer], numberOfSetsToWin: Int, setTargetScore: Int): Future[RoundRobinGroup] = {
    db.run(roundRobinCollection += RoundRobinGroupDb(None, seriesRoundId)).flatMap { insertedIndex =>
      logger.info(insertedIndex.toString)
      Future.sequence {robinPlayers.map(player =>
        createRobinPlayer(player.copy(robinGroupId = Some(insertedIndex))))}
        .flatMap { playersList =>
        matchDb.createRobinMatches(playersList, numberOfSetsToWin, setTargetScore).map { robinMatches =>
          RoundRobinGroup(Some(insertedIndex), seriesRoundId, playersList, robinMatches)
        }
      }
    }
  }

  // clearing to redraw! Matches should be cleared by matchDb
  def clearRobin(seriesRoundId: Int): Unit ={
    db.run(roundRobinCollection.filter( robin => robin.seriesRoundId === seriesRoundId).delete)
  }

  def createRobinPlayer(robinPlayer: RobinPlayer): Future[RobinPlayer] ={
    db.run(robinPlayerCollection += robinPlayer).map { _ => robinPlayer}
  }

  private class RoundRobinTable(tag: Tag) extends Table[RoundRobinGroupDb](tag, "ROBIN_ROUNDS") {

    def id = column[Int]("ROUND_ROBIN_ID", O.PrimaryKey, O.AutoInc)
    def seriesRoundId = column[Int]("SERIESROUND_ID")

    def * = (id.?, seriesRoundId) <> ((RoundRobinGroupDb.apply _ ).tupled, RoundRobinGroupDb.unapply)
  }

  private class RobinPlayerTable(tag: Tag) extends Table[RobinPlayer](tag, "ROBIN_PLAYERS") {

    def id = column[Int]("ROBIN_PLAYER_ID", O.PrimaryKey, O.AutoInc)
    def roundRobinGroupId = column[Int]("ROBIN_GROUP_ID")

    def seriesPlayerId = column[Int]("SERIES_PLAYER_ID")
    def rankValue = column[Int]("RANK_VALUE")
    def robinNr = column[Int]("ROBIN_NR")

    def wonMatches = column[Int]("WON_MATCHES")
    def lostMatches = column[Int]("LOST_MATCHES")
    def wonSets = column[Int]("WON_SETS")
    def lostSets = column[Int]("LOST_SETS")
    def wonPoints = column[Int]("WON_POINTS")
    def lostPoints = column[Int]("LOST_POINTS")

    def robinRoundGroup = foreignKey("FK_ROBINPLAYER_ROBIN_GROUP", roundRobinGroupId ,roundRobinCollection)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

    def * = (id.?, roundRobinGroupId.?, seriesPlayerId, rankValue, robinNr, wonMatches, lostMatches, wonSets, lostSets, wonPoints, lostPoints) <> (RobinPlayer.tupled, RobinPlayer.unapply)
  }

}
