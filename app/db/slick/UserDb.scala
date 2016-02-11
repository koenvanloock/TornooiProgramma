package db.slick

import com.google.inject.Inject
import models.{Role, AuthUser}
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

  private class AuthUserTable(tag: Tag) extends Table[AuthUser](tag, "USERS") {

    def id = column[String]("USER_ID", O.PrimaryKey, O.Length(100))
    def name = column[String]("USERNAME")
    def pwd = column[String]("PASSWORD")
    def roleId = column[String]("ROLE_ID")

    def role = foreignKey("FK_USERS_ROLES", roleId, Roles)(_.roleId, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

    def * = (id.?, name, pwd, roleId.?) <> ((AuthUser.apply _ ).tupled, AuthUser.unapply)
  }

  private class RolesTable(tag: Tag) extends Table[Role](tag, "ROLES"){
    def roleId = column[String]("ROLE_ID", O.PrimaryKey, O.Length(100))
    def roleName = column[String]("ROLE_NAME")

    def * = (roleId.?, roleName) <> (Role.tupled, Role.unapply)
  }
}
