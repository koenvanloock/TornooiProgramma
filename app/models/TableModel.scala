package models

import slick.lifted.Rep

/**
  * @author Koen Van Loock
  * @version 1.0 24/01/2016 20:43
  */
trait TableModel[TM] {
  def getRepId(tm: TM): Rep[String]
}
