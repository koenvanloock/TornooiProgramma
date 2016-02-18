package db.slick

import com.google.inject.Inject
import models.{AuthUserTable, RolesTable, Role, AuthUser}
import org.slf4j.{Logger, LoggerFactory}
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.JdbcProfile
import slick.jdbc.GetResult
import utils.DbUtils
import scala.concurrent.ExecutionContext.Implicits.global



import scala.concurrent.Future


class UserDb @Inject()(@NamedDatabase("user") protected val dbConfigProvider  : DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile]{

  import driver.api._
  implicit val getResult: GetResult[AuthUser] = GetResult(r => AuthUser(r.<<, r.<<, r.<<, r.<<))


  private final val logger: Logger = LoggerFactory.getLogger(classOf[UserDb])

  private def Roles = TableQuery[RolesTable]
  db.run(DBIO.seq(Roles.schema.create))

  private def Users = TableQuery[AuthUserTable]
  // create the db
  val schema = Users.schema
  db.run(DBIO.seq(schema.create))



  def createUser(username: String, password: String, roleId: String): Future[AuthUser] ={
      val id = Some(DbUtils.generateId)
      db.run(Users += AuthUser(id, username, password, Some(roleId))).map{ _ => ()
        AuthUser(id, username, password, Some(roleId))
      }
  }

  def createRole(roleId: String, roleName: String) ={
    db.run(Roles += Role(Some(roleId), roleName))
  }

  def getUser(name: String, password: String): Future[Option[AuthUser]] = {
    val query = sql"""SELECT * FROM USERS WHERE USERNAME=$name AND PASSWORD=$password""".as[AuthUser]
      logger.debug(query.statements.mkString(","))
      db.run(query).map(_.headOption)
  }

  def deleteAll = db.run(Users.delete)

}
