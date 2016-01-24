package db.slick

import com.google.inject.Inject
import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.JdbcProfile
import slick.jdbc.GetResult
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

case class SiteMatchDb(matchId: Option[Int], playerA: Int, playerB: Int, handicap: Int, isForB: Boolean, numberOfSetsToWin: Int, targetScore: Int)

case class SiteGameDb(gameId: Option[Int], matchId: Int, pointA: Int, pointB: Int)

class MatchDb @Inject()(@NamedDatabase("default") protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  implicit val getResult: GetResult[SiteMatchDb] = GetResult(r => SiteMatchDb(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))

  import driver.api._


  private val siteMatches = TableQuery[SiteMatchDbTable]
  private val siteGames = TableQuery[SiteGameDbTable]
  private val robinMatches = TableQuery[RobinMatchTable]

  val schema = siteMatches.schema ++ siteGames.schema ++ robinMatches.schema
  db.run(DBIO.seq(schema.create))

  def getNextMatchId: Future[Int] = db.run(siteMatches.map(_.id).max.result).map( _.getOrElse(0) + 1)

  def createRobinMatches(robinId: Int, playersList: List[RobinPlayer], numberOfSetsToWin: Int, setTargetScore: Int): Future[List[SiteMatch]] = {
    Future.sequence {
      playersList.init.zipWithIndex.flatMap{ playerWithIndex =>
        val playerA = playerWithIndex._1
        playersList.drop(playerWithIndex._2 + 1).map { playerB => {
          val sets = (0 until (numberOfSetsToWin * 2 - 1)).map(_ => SiteGame(0, 0)).toList
          val relHandicap = playerA.rankValue - playerB.rankValue
          val isForB = relHandicap > 0
          createDbMatch(SiteMatch(None, sets, playerA.seriesPlayerId, playerB.seriesPlayerId, Math.abs(relHandicap), isForB, setTargetScore, numberOfSetsToWin)).flatMap( siteMatch =>
            db.run(robinMatches += RobinMatch(None, robinId, siteMatch.matchId.get)).map( _ => siteMatch)
          )
        }
        }
      }
    }
  }

  def createDbMatch(siteMatch: SiteMatch): Future[SiteMatch] = {
    getNextMatchId.flatMap { nextMatchId =>
      db.run(siteMatches += SiteMatchDb(Some(nextMatchId), siteMatch.playerA, siteMatch.playerB, siteMatch.handicap, siteMatch.isHandicapForB, siteMatch.numberOfSetsToWin, siteMatch.targetScore))
        .flatMap { insertedMatchNr =>
          Future.sequence {
            siteMatch.sets
              .map(set => {
                db.run(siteGames += SiteGameDb(None, nextMatchId, set.pointA, set.pointB))
              }
                .map(_ => SiteGame(set.pointA, set.pointB)))
          }.map { sets =>
            siteMatch.copy(matchId = Some(nextMatchId), sets = sets)
          }
        }
    }
  }

  def deleteAll = {
    db.run(siteGames.delete)
    db.run(siteMatches.delete)
    //db.run(sqlu"""ALTER TABLE MATCHES AUTO_INCREMENT=1;""")
    //db.run(sqlu"""ALTER TABLE SETSTABLE AUTO_INCREMENT=1;""")
  }

  private class SiteMatchDbTable(tag: Tag) extends Table[SiteMatchDb](tag, "MATCHES") {

    def id = column[Int]("MATCH_ID", O.PrimaryKey, O.AutoInc)

    def playerA = column[Int]("PLAYER_A")

    def playerB = column[Int]("PLAYER_B")

    def handicap = column[Int]("HANDICAP")

    def isForB = column[Boolean]("IS_HANDICAP_FOR_B")

    def numberOfSetsToWin = column[Int]("NUMBER_OF_SETS_TO_WIN")

    def setTargetScore = column[Int]("SET_TARGET_SCORE")

    def * = (id.?, playerA, playerB, handicap, isForB, numberOfSetsToWin, setTargetScore) <>(SiteMatchDb.tupled, SiteMatchDb.unapply)
  }

  private class SiteGameDbTable(tag: Tag) extends Table[SiteGameDb](tag, "SETSTABLE") {

    def id = column[Int]("SET_ID", O.PrimaryKey, O.AutoInc)
    def matchId = column[Int]("MATCH_ID")
    def pointA = column[Int]("POINT_A")

    def pointB = column[Int]("POINT_B")
    def siteMatch = foreignKey("FK_GAME_MATCH", matchId , siteMatches)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)


    def * = (id.?, matchId, pointA, pointB) <>(SiteGameDb.tupled, SiteGameDb.unapply)
  }

  private class RobinMatchTable(tag: Tag) extends Table[RobinMatch](tag, "ROBINMATCHES"){
    def id = column[Int]("ROBIN_MATCH_ID")
    def robinId = column[Int]("ROBIN_ID")
    def matchId = column[Int]("MATCH_ID")

    def siteMatch = foreignKey("FK_ROBINMATCH_MATCH", matchId , siteMatches)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
    //def robinRound= foreignKey("FK_ROBINMATCH_ROBINROUND", robinId ,)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

    def * = (id.?, robinId, matchId) <> (RobinMatch.tupled, RobinMatch.unapply)
  }

}
