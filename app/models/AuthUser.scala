package models

import play.api.libs.json.Json

case class User(username: String,
                role: String)

object User{
  val userFormat = Json.format[User]
}

case class AuthUser(userId: Option[Int],
                    username: String,
                    password: String,
                    role: Option[Int])

object AuthUser{
  val authUserFormat = Json.format[AuthUser]
}

case class UserPostData(username: String,
                        password: String)

object UserPostData{
  val postFormat = Json.format[UserPostData]
}