package db.slick


import com.google.inject.Inject
import org.slf4j.{LoggerFactory, Logger}
import play.api.db.slick.{HasDatabaseConfig, DatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.JdbcProfile
import slick.profile.RelationalProfile

import scala.concurrent.Future
  /*
class GenericDb[M, TM <: RelationalProfile#API#Table[M]] @Inject()(@NamedDatabase("user") protected val dbConfig: DatabaseConfigProvider)(dbName: String) extends HasDatabaseConfig[JdbcProfile] {
  import driver.api._
  private final val logger: Logger = LoggerFactory.getLogger(classOf[GenericDb[M, TM]])

  def retrieveByFields(optionalFieldsMap: Option[Map[String, String]]): Future[Option[M]] =
    flatMapToOption("retrieve by fields")(retrieveAllByFields(optionalFieldsMap))


  def retrieveAllByFields(optionalFieldsMap: Option[Map[String, String]]): Future[List[M]] = {
    val msg = s"retrieveAllByFields( $optionalFieldsMap )"
    logger.debug(msg)
    val query = optionalFieldsMap match {
      case None =>
        sql"SELECT * FROM #$dbName".as[M]
      case Some(fieldsMap) if fieldsMap.isEmpty =>
        sql"SELECT * FROM #$dbName".as[M]
      case Some(fieldsMap) =>
        val filters = fieldsMapToFilters(fieldsMap)
        sql"SELECT * FROM #$dbName WHERE #$filters".as[M]
    }
    logger.debug("\u001B[31mGenerated SQL statements: " + query.statements + "\u001B[30m")
    db.run(query).map(_.toList)
  }



  private def flatMapToOption(message: String): Future[List[M]] => Future[Option[M]] =
    futureList =>
      futureList.flatMap {
        case Nil =>
          val msg = s"$message ko"
          logger.debug(msg)
          Future(None)
        case r_m :: _ =>
          val msg = s"$message ok: $r_m"
          logger.debug(msg)
          Future(Some(r_m))
      }

  protected def fieldsMapToFilters(fieldsMap: Map[String, String]): String = {
    val size = fieldsMap.size
    fieldsMap.zipWithIndex.foldLeft("") {
      (s, indexedFieldEntry) => indexedFieldEntry match {
        case ((fieldKey, fieldValue), index) if index == size - 1 =>
          s + s"$fieldKey='$fieldValue'"
        case ((fieldKey, fieldValue), _) =>
          s + s"$fieldKey='$fieldValue' AND "
      }
    }
  }
}*/
