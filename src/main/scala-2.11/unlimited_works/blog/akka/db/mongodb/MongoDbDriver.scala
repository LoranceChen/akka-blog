package unlimited_works.blog.akka.db.mongodb

import org.mongodb.scala.model.Projections._
import org.mongodb.scala.{Observable, MongoClient}
import org.mongodb.scala.bson.collection.immutable.Document
import unlimited_works.blog.util.Config
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
/**
  *
  */
class MongoDbDriver(connUrl: String) extends DriverHelper {
//  val clients = mutable.Map[String, MongoClient]()
//
//  val config = unlimited_works.blog.akka.config.appConfig.mongo
//  val defaultClientAddress = s"mongodb://${config.username}:${config.password}@${config.address}:${config.port}/${config.database}"
//  clients += (defaultClientAddress -> mongoClient(defaultClientAddress))
//  println(s"$getClass - " + clients)
//
//  def mongoClient(address: String): MongoClient = {
//    println("mongo client link at " + address)
//    MongoClient(address)
//  }
  val conn = MongoClient(connUrl)
  val blogDb = conn.getDatabase("blog")
  lazy val close = {
    conn.close()
  }

  /**
    * mongoClient.getDatabase("blog").getCollection("account")
    *
    *   val hellowroldDb = mongoClient.getDatabase("blog")
    * val blogCollection = hellowroldDb.getCollection("blogs")
    */

  lazy val accountColl = blogDb.getCollection("account")
  def checkMailNotUsed(mail: String) = {
    accountColl.find(Document("email" -> mail)).projection(include("_id")).headOpt().map(_.isEmpty)
  }

}

trait DriverHelper {
  implicit class FindObservableEx[T](doc: Observable[T]) {
    def headOpt(): Future[Option[T]] = {
      doc.head().map(Some(_)).recover{
        case e: IllegalStateException => None
      }
    }
  }
}

object MongoDbDriver {

  val mongoInstance = new MongoDbDriver(Config.Mongo.connStr)
}