package models

import play.api.libs.json.OFormat
import slick.jdbc.GetResult

/**
  * @author Koen Van Loock
  * @version 1.0 24/01/2016 20:41
  */
trait Crudable[M] extends OFormat[M]{
  implicit val getResult: GetResult[M]
  def getId(m: M): Option[Int]
  def setId(id: Int)(m: M): M
}
