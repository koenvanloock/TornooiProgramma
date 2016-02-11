package models

import play.api.libs.json
import play.api.libs.json._
import slick.jdbc.GetResult

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