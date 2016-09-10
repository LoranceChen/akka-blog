package unlimited_works.blog.util

import scredis.Redis

/**
  *
  */
object BlogRedis {
  val client = new Redis(host = "127.0.0.1", port = 7777)
}
