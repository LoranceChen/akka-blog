package unlimited_works.blog.akka

import unlimited_works.blog.akka.DbConnector.CheckMailNotUsed
import unlimited_works.blog.akka.db.mongodb.MongoDbDriver

import akka.actor.{ Props, Actor}
import akka.pattern._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  *
  */
class DbConnector extends Actor{
  val dbInst = MongoDbDriver.mongoInstance
  def receive = {
    case CheckMailNotUsed(mail) =>
      dbInst.checkMailNotUsed(mail).pipeTo(sender())
  }

  //todo 复习actor的生命周期
  override def postStop(): Unit = {
    super.postStop()
    dbInst.close
  }
}

object DbConnector {
  def props = Props(classOf[DbConnector])

  case class CheckMailNotUsed(mail: String)
}