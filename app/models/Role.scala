package models

import play.api.libs.json.{JsObject, JsResult, JsValue}
import slick.jdbc.GetResult

case class Role(roleId: Option[Int], roleName: String)
