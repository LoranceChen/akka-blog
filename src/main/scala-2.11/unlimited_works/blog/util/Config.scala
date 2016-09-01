package unlimited_works.blog.util

import com.typesafe.config.ConfigFactory

/**
  *
  */
object Config {
  object CookieSession{
    val GOD_SESSION = "GOD_SESSION"
    val ACCOUNT_ID = "accountId"
    val REMEMBER_ME = "remember_me"

    val VISITOR = "visitor"
  }

  case class RememberMeData(account: String, password: String)

  val local = ConfigFactory.load("local")
  val dev = ConfigFactory.load("develop")
  val online = ConfigFactory.load("online")
  val devBackToOnline = local.withFallback(dev).withFallback(online)

  val blogPath = "unlimited_works.blog"
  val blogConf = devBackToOnline.getConfig(blogPath)

  object Email {
    val user = blogConf.getString("email.user")
    val password = blogConf.getString("email.password")
  }

//  s"mongodb://${config.username}:${config.password}@${config.address}:${config.port}/${config.database}"
  object Mongo {
    val mongo = blogConf.getConfig("mongo")
    val connStr = s"mongodb://${mongo.getString("username")}:${mongo.getString("password")}@${mongo.getString("host")}:${mongo.getString("port")}/${mongo.getString("database")}"

  }
}
