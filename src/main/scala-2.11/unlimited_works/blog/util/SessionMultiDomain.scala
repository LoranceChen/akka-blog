package unlimited_works.blog.util

//import com.redis.RedisClient
import spray.http.HttpRequest
import spray.routing.RequestContext
import scredis._

import scala.concurrent.{ExecutionContext, Future}

/**
  * should be save a specify place,such as a memcache server
  */
object SessionMultiDomain {
  var redisClient =  Redis() //new RedisClient("127.0.0.1", 6379)

  def puts(id1: String, data: Map[String, String])(implicit ec: ExecutionContext) : Future[Unit] = {
    redisClient.hmSet(id1, data)
  }

  def put(id1: String, data: (String, String))(implicit ec: ExecutionContext) : Future[Boolean] = {
    redisClient.hSet(id1, data._1, data._2) //false if override older value
  }

  def removes(id1: String, id2: List[String])(implicit ec: ExecutionContext) : Future[Long] = {
    redisClient.hDel(id1, id2: _*)
  }

  def removeAll(id1: String)(implicit ec: ExecutionContext) : Future[Long] = {
    redisClient.del(id1)
  }

  def remove(id1: String, id2: String)(implicit ec: ExecutionContext) : Future[Long] = {
    redisClient.hDel(id1, id2)
  }

  def gets(id1: String, id2: List[String])(implicit ec: ExecutionContext) : Future[Option[Map[String, String]]] = {
    redisClient.hGetAll(id1).map{x =>
      x.map(_.filter(y => id2.contains(y._1)))
    }
  }

  def getAll(id1: String)(implicit ec: ExecutionContext) : Future[Option[Map[String, String]]] = {
    redisClient.hGetAll(id1)
  }

  def get(id1: String, id2: String)(implicit ec: ExecutionContext) : Future[Option[String]] = {
    redisClient.hGet(id1, id2)
  }

  def getAccountId[A](req: HttpRequest)(implicit ec: ExecutionContext) : Future[Option[String]] = {
    Helpers.toFutureOpt(req.cookies.find(_.name == Config.CookieSession.GOD_SESSION).map(_.content).map{ x =>
      val y = get(x, Config.CookieSession.ACCOUNT_ID)
      y
    }).map(_.flatten)
  }

}
