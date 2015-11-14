package helpers

import anorm._
import org.specs2.execute.{Result, AsResult}
import play.api.db.DB
import play.api.test.WithApplication


abstract class WithDbTestUser extends WithApplication {
  override def around[T: AsResult](t: => T): Result = super.around {
    setupData()
    val testResult = t
    tearDown()

    testResult
  }

  def setupData() = {
        DB.withConnection{
          implicit conn =>
            SQL("TRUNCATE players").execute()
            SQL("INSERT INTO players(firstname,lastname,rank) VALUES('Koen','Van Loock',9)").execute()
        }
    }


  def tearDown() = {
  }
}
