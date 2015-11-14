package db

import javax.inject.Inject

import controllers.TournamentWrites
import models.Tournament

import play.api.libs.json.Json
import play.api.mvc.Controller
import play.modules.reactivemongo.{MongoController, ReactiveMongoComponents, ReactiveMongoApi}
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.MongoDriver
import reactivemongo.api.commands.WriteResult
import utils.DbUtils
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class TournamentDb extends TournamentWrites {
    def getCount = {
      val driver = new MongoDriver
      val connection = driver.connection(List("localhost:27017"))("pipoka_test")
      val db = connection.collection[JSONCollection]("tournaments")
      val result = db.count()
      driver.close()
      result
    }


    def getTournament(tournamentId: String): Future[Option[Tournament]] = {
      val driver = new MongoDriver
      val connection = driver.connection(List("localhost:27017"))("pipoka_test")
      val db = connection.collection[JSONCollection]("tournaments")
      val result = db
        .find(Json.obj("tournamentId" -> tournamentId))
        .cursor[Tournament]().collect[List](upTo = 1).map(_.headOption)
      driver.close()
      result
    }
      /*DB.withConnection{ implicit conn =>
      SQL("""SELECT * FROM tournaments WHERE tournamentId = {tournamentId}""")
        .on("tournamentId"-> tournamentId).apply().headOption.map(parseTournamentFromRow)
    }*/

      def updateTournament(tournamentToUpdate : Tournament) = {val driver = new MongoDriver
        val connection = driver.connection(List("localhost:27017"))("pipoka_test")
        val db = connection.collection[JSONCollection]("tournaments")
        val result = db.update(Json.obj("tournamentId" -> tournamentToUpdate.tournamentId), tournamentToUpdate)
        driver.close()
        result
      }

      /*{
        DB.withConnection { implicit c =>
          tournamentToUpdate.tournamentId.map( id => SQL("""
                  UPDATE tournaments
                  SET tournamentName = {name}, tournamentDate = {date},
                  maxSeriesEntries = {maxSeriesEntries}, hasMultipleSeries = {hasMultipleSeries},
                  showClub = {showClub}
                  WHERE tournamentId = {id}
              """).on("id" -> id,
              "name" -> tournamentToUpdate.tournamentName,
              "date" -> DbUtils.localDateToString(tournamentToUpdate.tournamentDate),
              "maxSeriesEntries"-> tournamentToUpdate.maximumNumberOfSeriesEntries,
              "hasMultipleSeries" -> tournamentToUpdate.hasMultipleSeries,
              "showClub" -> tournamentToUpdate.showClub).executeUpdate()
          )
        }
      }*/

      def getTournaments: Future[List[Tournament]] = {
        val driver = new MongoDriver
        val connection = driver.connection(List("localhost:27017"))("pipoka_test")
        val db = connection.collection[JSONCollection]("tournaments")
        val result = db.find(Json.obj()).cursor[Tournament]().collect[List]()
        driver.close()
        result
      }

      /*DB.withConnection { implicit c => SQL("SELECT * from tournaments").apply().toList.map(parseTournamentFromRow) }*/


      def insertTournament(tournamentToInsert: Tournament): Future[String] = {
        val driver = new MongoDriver
        val connection = driver.connection(List("localhost:27017"))("pipoka_test")
        val db = connection.collection[JSONCollection]("tournaments")
        val result = getCount.flatMap { count =>
          val nextId = (count + 1).toString
          db.insert(tournamentToInsert.copy(tournamentId = Some(nextId))).map(_ => nextId)
          }
          driver.close()
          result
        }

      /*DB.withConnection { implicit c =>

        val id: Option[Long] = SQL("""
        INSERT INTO tournaments(tournamentName,tournamentDate,maxSeriesEntries, hasMultipleSeries, showClub)
        VALUES({name}, {date}, {maxSeriesEntries}, {hasMultipleSeries}, {showClub})""")
          .on("name" -> tournamentToInsert.tournamentName,
              "date" -> DbUtils.localDateToString(tournamentToInsert.tournamentDate),
              "maxSeriesEntries" -> tournamentToInsert.maximumNumberOfSeriesEntries,
              "hasMultipleSeries" -> tournamentToInsert.hasMultipleSeries,
              "showClub" -> tournamentToInsert.showClub).executeInsert()
        id.map(_.toInt)
      }*/

    def deleteTournament(tournamentId: String) = {val driver = new MongoDriver
      val connection = driver.connection(List("localhost:27017"))("pipoka_test")
      val db = connection.collection[JSONCollection]("tournaments")
      val result = db.remove(Json.obj("tournamentId" -> tournamentId))
    driver.close()
    result
    }

    /*DB.withConnection{ implicit conn =>
      SQL("""DELETE FROM tournaments WHERE tournamentId = {tournamentId}""").on("tournamentId" -> tournamentId).executeUpdate()
    }*/



/*
  def parseTournamentFromRow: (Row) => Tournament = { row =>
    Tournament(row[Option[Long]]("tournamentId"),
      row[String]("tournamentName"),
      DbUtils.dbDateToLocalDate(row[DateTime]("tournamentDate")),
      row[Int]("maxSeriesEntries"),
      row[Boolean]("hasMultipleSeries"),
      row[Boolean]("showClub"))
  }*/
}
