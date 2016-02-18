package models

import play.api.libs.json
import play.api.libs.json._
import scala.language.postfixOps
import slick.driver.MySQLDriver.api.{TableQuery => _, _}
import play.api.libs.json.{JsResult, JsValue, Json, JsObject}
import slick.jdbc.GetResult
import slick.lifted.{Tag, TableQuery}
import slick.model.ForeignKeyAction._
import slick.lifted.TableQuery
import play.api.libs.functional.syntax._

case class User(username: String,
                role: String)

object User{
  val userFormat = Json.format[User]
}

case class AuthUser(userId: Option[String],
                    username: String,
                    password: String,
                    role: Option[String])

object AuthUser extends Crudable[AuthUser] with OFormat[AuthUser]{
  override implicit val getResult: GetResult[AuthUser] = GetResult(r => AuthUser(r.<<, r.<<, r.<<, r.<<))

  override def getId(m: AuthUser): Option[String] = m.userId

  override def setId(id: String)(m: AuthUser): AuthUser = m.copy(userId=Some(id))

  override def writes(o: AuthUser): JsObject = Json.writes[AuthUser].asInstanceOf[JsObject]

  implicit val authReads= Json.reads[AuthUser]
  override def reads(json: JsValue): JsResult[AuthUser] = json.validate[AuthUser]
}

case class UserPostData(username: String,
                        password: String)

object UserPostData{
  val postFormat = Json.format[UserPostData]
}

class AuthUserTable(tag: Tag) extends Table[AuthUser](tag, "USERS") {
  val Roles = TableQuery[RolesTable]
  def id = column[String]("USER_ID", O.PrimaryKey, O.Length(100))
  def name = column[String]("USERNAME")
  def pwd = column[String]("PASSWORD")
  def roleId = column[String]("ROLE_ID")

  def role = foreignKey("FK_USERS_ROLES", roleId, Roles)(_.roleId, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

  def * = (id.?, name, pwd, roleId.?) <> ((AuthUser.apply _ ).tupled, AuthUser.unapply)
}