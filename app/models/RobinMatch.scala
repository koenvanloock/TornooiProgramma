package models

import slick.driver.MySQLDriver.api.{TableQuery => _, _}
import slick.lifted.Tag

import scala.language.postfixOps

case class RobinMatch(robinMatchId: Option[String], robinId: String, matchId: String)


