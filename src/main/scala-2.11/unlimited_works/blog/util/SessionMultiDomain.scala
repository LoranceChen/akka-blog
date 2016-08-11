package unlimited_works.blog.util

import com.redis.RedisClient
import spray.http.HttpRequest
import spray.routing.RequestContext

/**
  * should be save a specify place,such as a memcache server
  */
object SessionMultiDomain {
  var redisClient = new RedisClient("127.0.0.1", 6379)

  def puts(id1: String, data: Map[String, String]): Unit = {
    redisClient.hmset(id1, data)
  }

  def put(id1: String, data: (String, String)): Unit = {
    redisClient.hset(id1, data._1, data._2) //false if override older value
  }

  def removes(id1: String, id2: List[String]): Unit = {
    redisClient.hdel(id1, id2)
  }

  def removeAll(id1: String): Unit = {
    redisClient.del(id1)
  }

  def remove(id1: String, id2: String): Unit = {
    redisClient.hdel(id1, id2)
  }

  def gets(id1: String, id2: List[String]): Unit = {
    redisClient.hgetall(id1).map{x =>
      x.filter(y => id2.contains(y._1))
    }
  }

  def getAll(id1: String) = {
    redisClient.hgetall(id1)
  }

  def get(id1: String, id2: String) = {
    redisClient.hget(id1, id2)
  }

  def getAccountId[A](req: HttpRequest) = {
    req.cookies.find(_.name == Config.CookieSession.GOD_SESSION).map(_.content).flatMap{ x =>
      val y = get(x, Config.CookieSession.ACCOUNT_ID)
      y
    }
  }

}
