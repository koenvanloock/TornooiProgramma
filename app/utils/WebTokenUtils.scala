package utils

import java.time.{LocalDateTime, ZoneOffset}

import authentikat.jwt._
import models.AuthUser
import play.api.Play.current
import play.api.mvc.{AnyContent, Request, RequestHeader}


object WebTokenUtils {
  val jsonTokenKey = current.configuration.getString("jwtKey").get

  def getToken(requestHeader: RequestHeader) = for {
    authHeader <- requestHeader.headers.get("Authorization")
    authToken <- authHeader.split(' ').drop(1).headOption
  } yield authToken

  def createJWT(user: AuthUser): String = {
    val header = JwtHeader("HS256")
    val claimsSet = JwtClaimsSet(
      Map(
        "username" -> user.username,
        "role"     -> user.role,
        "exp"      -> LocalDateTime.now.plusSeconds(12 * 3600).toEpochSecond(ZoneOffset.UTC))
    )
    JsonWebToken(header, claimsSet, jsonTokenKey)
  }

  def validateToken(jsonWebToken: String): Boolean =
    JsonWebToken.validate(jsonWebToken, jsonTokenKey) && !isExpired(jsonWebToken)

  def isExpired(jsonWebToken: String): Boolean = {
    val token = JsonWebToken.unapply(jsonWebToken).get
    val expirationTime = token._2.asSimpleMap.get("exp").toLong

    expirationTime < LocalDateTime.now.toEpochSecond(ZoneOffset.UTC)
  }

  def getRole(request: Request[AnyContent]) = {
    val token = getToken(request).flatMap(JsonWebToken.unapply).get
    token._2.asSimpleMap.get("role")
  }

  def getUser(request: Request[AnyContent]) = {
    val token = getToken(request).flatMap(JsonWebToken.unapply).get
    token._2.asSimpleMap.get("username")
  }
}