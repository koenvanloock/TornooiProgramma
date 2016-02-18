package db.slick

import com.google.inject.Inject
import models.{PlayerTable, Rank, Player}
import org.slf4j.{Logger, LoggerFactory}
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.JdbcProfile
import slick.jdbc.GetResult
import utils.{DbUtils, RankConverter}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class PlayerDb @Inject()(@NamedDatabase("default") protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile]{

  implicit val getResult: GetResult[Player] = GetResult(r => Player(r.<<, r.<<, r.<<, utils.RankConverter.getRankOfInt(r.nextInt())))
  import driver.api._

  private final val logger: Logger = LoggerFactory.getLogger(classOf[PlayerDb])
  // create the db
  val schema = PlayerCollection.schema
  db.run(DBIO.seq(schema.create))

  implicit def rankColumnType = MappedColumnType.base[Rank, Int](
    rank => rank.value,
    rankValue => RankConverter.getRankOfInt(rankValue)
  )

  private def PlayerCollection = TableQuery[PlayerTable]

  def insertPlayer(playerToInsert: Player): Future[Option[Player]] = {
    val playerWithId = playerToInsert.copy(playerId = Some(DbUtils.generateId))
    db.run(PlayerCollection += playerWithId).map(_ => Some(playerWithId))
  }

  def updatePlayer(player: Player): Future[Option[Player]] = db.run(PlayerCollection.filter(_.id === player.playerId).update(player)).map{ _ => (); Some(player) }

  def deletePlayer(playerId: String): Future[Int] = db.run(PlayerCollection.filter(_.id === playerId).delete)


  def getPlayer(playerId: String): Future[Option[Player]] = db.run(PlayerCollection.filter(_.id === playerId).result).map(_.headOption)

  def getAllPlayers: Future[List[Player]] = db.run(PlayerCollection.result).map(_.toList)

  def deleteAll = db.run(sql"""DELETE FROM PLAYERS""".as[Int])

}
