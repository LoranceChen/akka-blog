package unlimited_works.blog.util

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
}
