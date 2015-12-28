package db.slick


import com.google.inject.Inject
import models._
import org.slf4j.{Logger, LoggerFactory}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.JdbcProfile
import slick.jdbc.GetResult

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SeriesRoundDb @Inject()(@NamedDatabase("default") protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  implicit val getResult: GetResult[GenericSeriesRound] = GetResult(r => GenericSeriesRound(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))

  import driver.api._

  private final val logger: Logger = LoggerFactory.getLogger(classOf[SeriesDb])

  def getMaxId: Future[Option[Int]] = db.run(sql"""SELECT max(SERIES_ROUND_ID) FROM SERIES_ROUNDS""".as[Int]).map(_.headOption)

  def getNextNr(seriesId: Int): Future[Int] = db.run(sql"""SELECT count(*) FROM SERIES_ROUNDS WHERE SERIES_ID = $seriesId""".as[Int]).map(_.head + 1)


  private def SeriesCollection = TableQuery[GenericSeriesRoundTable]

  // create the db
  val schema = SeriesCollection.schema
  db.run(DBIO.seq(schema.create))

  def insertSeriesRound(seriesRound: SeriesRound): Future[Option[SeriesRound]] = {
    val seriesRoundToInsert = convertToGenericRound(seriesRound)
    getMaxId.flatMap { id =>
      getNextNr(seriesRoundToInsert.seriesId).flatMap{ nextRoundNr =>
        db.run(SeriesCollection += seriesRoundToInsert.copy(seriesRoundId = id, roundNr = nextRoundNr)).map { _ => ()
          Some(convertGenericToSeriesRound(seriesRoundToInsert.copy(seriesRoundId = id, roundNr = nextRoundNr)))
        }
    }
  }
}

def updateSeriesRound(seriesRound: SeriesRound): Future[Option[SeriesRound]] = {
val seriesRoundToUpdate = convertToGenericRound (seriesRound)
  val query = SeriesCollection.update(seriesRoundToUpdate)
db.run (SeriesCollection.filter(_.id === seriesRoundToUpdate.seriesRoundId).update(seriesRoundToUpdate) ).map {
_ => ()
Some (convertGenericToSeriesRound (seriesRoundToUpdate) )
}
}

def convertToGenericRound (seriesRound: SeriesRound): GenericSeriesRound = seriesRound match {
case robin: RobinRound => GenericSeriesRound (robin.seriesRoundId, None, Some (robin.numberOfRobins), robin.roundType, robin.seriesId, robin.roundNr)
case bracket: SiteBracketRound => GenericSeriesRound (bracket.seriesRoundId, Some (bracket.numberOfBracketRounds), None, bracket.roundType, bracket.seriesId, bracket.roundNr)
}

def convertGenericToSeriesRound (genericRound: GenericSeriesRound): SeriesRound = genericRound.roundType match {
case "R" => RobinRound (genericRound.seriesRoundId, genericRound.numberOfRobins.get, genericRound.roundType, genericRound.seriesId, genericRound.roundNr)
case "B" => SiteBracketRound (genericRound.seriesRoundId, genericRound.numberOfBracketRounds.get, genericRound.roundType, genericRound.seriesId, genericRound.roundNr)
case _ => SiteBracketRound (genericRound.seriesRoundId, genericRound.numberOfBracketRounds.get, genericRound.roundType, genericRound.seriesId, genericRound.roundNr)
}

def getRoundsListOfSeries (seriesId: Int): Future[List[SeriesRound]] = {
db.run (sql"""SELECT * FROM SERIES_ROUNDS WHERE SERIES_ID = $seriesId ORDER BY ROUND_NR ASC""".as[GenericSeriesRound].map (_.toList.map (convertGenericToSeriesRound) ) )
}

private class GenericSeriesRoundTable(tag: Tag) extends Table[GenericSeriesRound](tag, "SERIES_ROUNDS") {

  def id = column[Int]("SERIES_ROUND_ID", O.PrimaryKey, O.AutoInc)

  def numberOfBrackets = column[Option[Int]]("NUMBER_OF_BRACKETS")

  def numberOfRobins = column[Option[Int]]("NUMBER_OF_ROBINS")

  def roundType = column[String]("ROUND_TYPE")

  def seriesId = column[Int]("SERIES_ID")

  def roundNr = column[Int]("ROUND_NR")


  def * = (id.?, numberOfBrackets, numberOfRobins, roundType, seriesId, roundNr) <>((GenericSeriesRound.apply _).tupled, GenericSeriesRound.unapply)
}

}
