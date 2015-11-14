package helpers

import anorm._
import org.specs2.execute.{Result, AsResult}
import play.api.db.DB
import play.api.test.WithApplication

class WithDbTestSeriesPlayer extends WithApplication {
  override def around[T: AsResult](t: => T): Result = super.around {
    setupData()
    val testResult = t
    tearDown()

    testResult
  }

  def setupData() = {
    DB.withConnection{
      implicit conn =>
        SQL("TRUNCATE tournamentSeries").execute()
        SQL("""INSERT INTO tournamentSeries VALUES(1,"Open zonder voorgift", "#ffffff",2,21,true,0,true,1)""").execute()
        SQL("TRUNCATE seriesPlayers").execute()
        SQL("""INSERT INTO seriesPlayers(firstname, lastname, rank, club, seriesId) VALUES('Luk','Geraets',8,'TTC Pipoka',1)""").execute()

    }
  }


  def tearDown() = {
  }
}
