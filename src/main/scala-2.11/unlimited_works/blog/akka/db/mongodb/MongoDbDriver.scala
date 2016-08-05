package unlimited_works.blog.akka.db.mongodb

import org.mongodb.scala.MongoClient
import scala.collection.mutable
/**
  *
  */
object MongoDbDriver {
  val clients = mutable.Map[String, MongoClient]()

  val config = unlimited_works.blog.akka.config.appConfig.mongo
  val defaultClientAddress = s"mongodb://${config.username}:${config.password}@${config.address}:${config.port}/${config.database}"
  clients += (defaultClientAddress -> mongoClient(defaultClientAddress))
  println(s"$getClass - " + clients)

  def mongoClient(address: String): MongoClient = {
    println("mongo client link at " + address)
    MongoClient(address)
  }

  /**
    * mongoClient.getDatabase("blog").getCollection("account")
    *
    *   val hellowroldDb = mongoClient.getDatabase("blog")
        val blogCollection = hellowroldDb.getCollection("blogs")
    */
}
