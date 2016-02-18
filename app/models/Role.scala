package models

import java.time.LocalDate
import scala.language.postfixOps
import slick.driver.MySQLDriver.api.{TableQuery => _, _}
import play.api.libs.json.{JsResult, JsValue, Json, JsObject}
import slick.jdbc.GetResult
import slick.lifted.{Tag, TableQuery}
import slick.model.ForeignKeyAction._
import slick.lifted.TableQuery
import play.api.libs.functional.syntax._

case class Role(roleId: Option[String], roleName: String)

class RolesTable(tag: Tag) extends Table[Role](tag, "ROLES"){
  def roleId = column[String]("ROLE_ID", O.PrimaryKey, O.Length(100))
  def roleName = column[String]("ROLE_NAME")

  def * = (roleId.?, roleName) <> (Role.tupled, Role.unapply)
}