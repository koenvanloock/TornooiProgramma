package db.slick

import com.google.inject.Inject
import models.{Rank, GenericSeriesRound, SeriesPlayer, Player}
import play.api.Logger
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.JdbcProfile
import slick.jdbc.GetResult
import utils.RankConverter
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class SeriesPlayersDb @Inject()(@NamedDatabase("default") protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile]  {

  implicit val getResult: GetResult[SeriesPlayer] = GetResult(r => SeriesPlayer(r.<<, r.<<, utils.RankConverter.getRankOfInt(r.nextInt()), r.<<,r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))
  import driver.api._

  implicit def rankColumnType = MappedColumnType.base[Rank, Int](
    rank => rank.value,
    rankValue => RankConverter.getRankOfInt(rankValue)
  )

  private def SeriesPlayerCollection = TableQuery[SeriesPlayerTable]

  // create the db
  val schema = SeriesPlayerCollection.schema
  db.run(DBIO.seq(schema.create))

  def create(seriesPlayer: SeriesPlayer): Future[SeriesPlayer] = {
    db.run(SeriesPlayerCollection += seriesPlayer).map { insertedIndex => ()
      Logger.info(insertedIndex.toString)
    seriesPlayer.copy(seriesPlayerId = Some(insertedIndex))
    }
  }

  def getPlayersOfSeries(seriesId: Int): Future[List[SeriesPlayer]] ={
    db.run(SeriesPlayerCollection.filter(_.seriesId === seriesId).result).map(_.toList)
  }

  def getSeriesOfPlayer(playerId: Int): Future[List[SeriesPlayer]] = {
    db.run(SeriesPlayerCollection.filter(_.playerId === playerId).result).map(_.toList)
  }

  def getSeriesSubscriptionOfPlayer(seriesId: Int, playerId: Int): Future[Option[SeriesPlayer]] = {
    db.run(SeriesPlayerCollection.filter(_.seriesId === seriesId).filter(_.playerId === playerId).result).map(_.headOption)
  }

  def deleteSubscriptions(seriesIdList: List[Int], playerId: Int) = {
    seriesIdList.map{ seriesId =>
      val query = SeriesPlayerCollection
        .filter(_.seriesId === seriesId)
        .filter(_.playerId === playerId).delete
    Logger.info(query.statements.mkString(","))
      db.run(query)
    }
  }

  private class SeriesPlayerTable(tag: Tag) extends Table[SeriesPlayer](tag, "SERIES_PLAYERS") {

    def id = column[Int]("SERIES_PLAYER_ID", O.PrimaryKey, O.AutoInc)

    def playerId = column[Int]("PLAYER_ID")

    def rank = column[Rank]("TOURNAMENT_RANK")

    def wonMatches = column[Int]("WON_MATCHES")
    def lostMatches = column[Int]("LOST_MATCHES")

    def wonSets = column[Int]("WON_SETS")
    def lostSets = column[Int]("LOST_SETS")

    def wonPoints = column[Int]("WON_POINTS")
    def lostPoints = column[Int]("LOST_POINTS")

    def seriesId = column[Int]("SERIES_ID")


    def * = (id.?, playerId, rank, wonMatches, lostMatches, wonSets, lostSets, wonPoints, lostPoints, seriesId) <>((SeriesPlayer.apply _).tupled, SeriesPlayer.unapply)
  }



}
