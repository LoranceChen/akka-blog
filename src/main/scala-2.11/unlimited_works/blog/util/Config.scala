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
  }

  case class RememberMeData(account: String, password: String)

  val dev = ConfigFactory.load("develop")
  val online = ConfigFactory.load("online")
  val devBackToOnline = dev.withFallback(online)

  val blogPath = "unlimited_works.blog"
  val blogConf = devBackToOnline.getConfig(blogPath)

  object Email {
    val user = blogConf.getString("email.user")
    val password = blogConf.getString("email.password")
  }
}
