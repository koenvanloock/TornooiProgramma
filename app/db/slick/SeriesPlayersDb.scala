package db.slick

import com.google.inject.Inject
import models._
import play.api.Logger
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.JdbcProfile
import slick.jdbc.GetResult
import utils.{DbUtils, RankConverter}
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
    val seriesPlayerToInsert = seriesPlayer.copy(seriesPlayerId = Some(DbUtils.generateId))
    db.run(SeriesPlayerCollection += seriesPlayerToInsert).map { _ => ()
    seriesPlayerToInsert
    }
  }

  def getSeriesPlayer(seriesPlayerId: String): Future[Option[SeriesPlayer]] = {
    db.run(SeriesPlayerCollection.filter(_.id === seriesPlayerId).result).map(_.headOption)
  }

  def getPlayersOfSeries(seriesId: String): Future[List[SeriesPlayer]] ={
    db.run(SeriesPlayerCollection.filter(_.seriesId === seriesId).result).map(_.toList)
  }

  def getSeriesOfPlayer(playerId: String): Future[List[SeriesPlayer]] = {
    db.run(SeriesPlayerCollection.filter(_.playerId === playerId).result).map(_.toList)
  }

  def getSeriesSubscriptionOfPlayer(seriesId: String, playerId: String): Future[Option[SeriesPlayer]] = {
    db.run(SeriesPlayerCollection.filter(_.seriesId === seriesId).filter(_.playerId === playerId).result).map(_.headOption)
  }

  def deleteSubscriptions(seriesIdList: List[String], playerId: String) = {
    seriesIdList.map{ seriesId =>
      val query = SeriesPlayerCollection
        .filter(_.seriesId === seriesId)
        .filter(_.playerId === playerId).delete
    Logger.info(query.statements.mkString(","))
      db.run(query)
    }
  }
}
