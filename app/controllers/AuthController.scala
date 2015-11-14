package controllers

import db.UserDb
import models.{UserPostData, AuthUser}
import play.api.Logger
import play.api.mvc.{Action, Controller}
import utils.WebTokenUtils

class AuthController  extends Controller {

  def login = Action { request =>
    val userData = for{
                          body <- request.body.asJson
                          user <- (body \ "username").asOpt[String]
                          password <- (body \ "password").asOpt[String]
                          dbUser <- UserDb.getUser(user, password)
    } yield dbUser

    userData match {
      case Some(user) => Ok(WebTokenUtils.createJWT(user))
      case None       => BadRequest
    }
  }
}