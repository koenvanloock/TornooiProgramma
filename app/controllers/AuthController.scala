package controllers

import javax.inject.Inject
import db.slick.UserDb
import models.AuthUser
import play.api.db.slick.SlickApi
import play.api.mvc.{Action, Controller}
import utils.WebTokenUtils
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class AuthController @Inject()(userDb: UserDb)  extends Controller {
  def login = Action.async { request =>
    val userOpt = for{
      body <- request.body.asJson
      user <- (body \ "username").asOpt[String]
      password <- (body \ "password").asOpt[String]
    } yield (user, password)

   userOpt.map( user => userDb.getUser(user._1, user._2).map(_.map(authUser => Ok(WebTokenUtils.createJWT(authUser))).getOrElse(BadRequest))
      ).getOrElse(Future(BadRequest))
  }
}