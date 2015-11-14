package db

import anorm._
import models.AuthUser
import play.api.Play.current
import play.api.db.DB

object UserDb {

  def getUser(username: String, password: String): Option[AuthUser] = DB.withConnection("user") { implicit  conn =>
    SQL("""
      SELECT usr.username, usr.password, role.roleName
      FROM users usr, roles role
      WHERE username = {username}
        AND password = {password}
        AND usr.roleId = role.roleId
        """).on("username" -> username,
        "password" -> password)
      .apply().headOption.map(convertRowToUser)
  }

  def convertRowToUser: (Row) => AuthUser = { row =>
    AuthUser(
      row[String]("username"),
      row[String]("password"),
      row[String]("roleName")
    )
  }

}