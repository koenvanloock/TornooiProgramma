package db.slick

import com.google.inject.Inject
import models.{SiteBracketRound, SeriesPlayer, BracketRound}
import models.BracketRound.BracketRoundTable
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.JdbcProfile
import utils.DbUtils
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class BracketDb @Inject()(@NamedDatabase("default") protected val dbConfigProvider: DatabaseConfigProvider)  extends HasDatabaseConfigProvider[JdbcProfile]{

  import driver.api._
  val BracketRoundCollection = TableQuery[BracketRoundTable]

  def drawBracket(seriesRoundId: String, seriesPlayers: List[SeriesPlayer], numberOfBracketRounds: Int): Future[List[BracketRound]] = {
    createRounds(seriesRoundId, seriesPlayers, numberOfBracketRounds)
  }

  def createRounds(seriesRoundId: String, seriesPlayers: List[SeriesPlayer], roundNrToCreate: Int):Future[List[BracketRound]] = {
    val roundToCreate =  BracketRound(Some(DbUtils.generateId), seriesRoundId, roundNrToCreate)
    db.run(BracketRoundCollection +=roundToCreate).flatMap( createdRound =>
      createRounds(seriesRoundId,seriesPlayers, roundNrToCreate-1)).map(roundList =>
      roundToCreate :: roundList)
  }

  val schema = BracketRoundCollection.schema
  db.run(DBIO.seq(schema.create))


  def createBracketRound(bracketRound: BracketRound) = {
    db.run(BracketRoundCollection += bracketRound.copy(id=Some(DbUtils.generateId))).map{ insertedRound => insertedRound}
  }

  def deleteBracketRound(bracketRoundId: String) = {
    db.run(BracketRoundCollection.filter(_.id === bracketRoundId).delete)
  }

}
