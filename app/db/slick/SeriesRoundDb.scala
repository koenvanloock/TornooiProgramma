package db.slick


import com.google.inject.Inject
import models._
import org.slf4j.{Logger, LoggerFactory}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.JdbcProfile
import slick.jdbc.GetResult
import utils.DbUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SeriesRoundDb @Inject()(@NamedDatabase("default") protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {


  implicit val getResult: GetResult[GenericSeriesRound] = GetResult(r => GenericSeriesRound(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))

  import driver.api._

  private final val logger: Logger = LoggerFactory.getLogger(classOf[SeriesDb])

  def getNextNr(seriesId: String): Future[Int] = db.run(sql"""SELECT count(*) FROM SERIES_ROUNDS WHERE SERIES_ID = $seriesId""".as[Int]).map(_.head + 1)


  private def SeriesRoundCollection = TableQuery[GenericSeriesRoundTable]

  // create the db
  val schema = SeriesRoundCollection.schema
  db.run(DBIO.seq(schema.create))

  def insertSeriesRound(seriesRound: SeriesRound): Future[Option[SeriesRound]] = {
    val seriesRoundToInsert = convertToGenericRound(seriesRound).copy(seriesRoundId = Some(DbUtils.generateId))

      getNextNr(seriesRoundToInsert.seriesId).flatMap { nextRoundNr =>
        db.run(SeriesRoundCollection += seriesRoundToInsert.copy(roundNr = nextRoundNr)).map { _ => ()
          Some(convertGenericToSeriesRound(seriesRoundToInsert.copy(roundNr = nextRoundNr)))
        }
      }
  }

  def getSeriesRound(seriesRoundId: String): Future[Option[SeriesRound]] = {
    db.run(SeriesRoundCollection.filter(_.id === seriesRoundId).result).map(_.headOption.map(convertGenericToSeriesRound))
  }

  def updateSeriesRound(seriesRound: SeriesRound): Future[Option[SeriesRound]] = {
    val seriesRoundToUpdate = convertToGenericRound(seriesRound)
    val query = SeriesRoundCollection.update(seriesRoundToUpdate)
    db.run(SeriesRoundCollection.filter(_.id === seriesRoundToUpdate.seriesRoundId).update(seriesRoundToUpdate)).map {
      _ => ()
        Some(convertGenericToSeriesRound(seriesRoundToUpdate))
    }
  }

  def convertToGenericRound(seriesRound: SeriesRound): GenericSeriesRound = seriesRound match {
    case robin: RobinRound => GenericSeriesRound(robin.seriesRoundId, None, Some(robin.numberOfRobins), robin.roundType, robin.seriesId, robin.roundNr)
    case bracket: SiteBracketRound => GenericSeriesRound(bracket.seriesRoundId, Some(bracket.numberOfBracketRounds), None, bracket.roundType, bracket.seriesId, bracket.roundNr)
  }

  def convertGenericToSeriesRound(genericRound: GenericSeriesRound): SeriesRound = genericRound.roundType match {
    case "R" => RobinRound(genericRound.seriesRoundId, genericRound.numberOfRobins.get, genericRound.roundType, genericRound.seriesId, genericRound.roundNr)
    case "B" => SiteBracketRound(genericRound.seriesRoundId, genericRound.numberOfBracketRounds.get, genericRound.roundType, genericRound.seriesId, genericRound.roundNr)
    case _ => SiteBracketRound(genericRound.seriesRoundId, genericRound.numberOfBracketRounds.get, genericRound.roundType, genericRound.seriesId, genericRound.roundNr)
  }

  def getRoundsListOfSeries(seriesId: String): Future[List[SeriesRound]] = {
    db.run(sql"""SELECT * FROM SERIES_ROUNDS WHERE SERIES_ID = $seriesId ORDER BY ROUND_NR ASC""".as[GenericSeriesRound].map(_.toList.map(convertGenericToSeriesRound)))
  }
}
