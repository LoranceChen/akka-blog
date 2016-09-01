package unlimited_works.blog.util.mail

import java.util.concurrent.ForkJoinPool

import unlimited_works.blog.util.Config

import scala.concurrent.{ExecutionContext, Future, blocking}

/**
  *
  */
object Email {
  lazy val pool = new ForkJoinPool(5)
  implicit val ex = ExecutionContext.fromExecutor(pool)
  def sendForRegister(inviteCode: String, to: String)(implicit ec: ExecutionContext) = Future {
    blocking(try{
      val password = Config.Email.password
      val user = Config.Email.user
      JavaEmail.sendMail(user, user, password, to, "[ScalaChan]获取注册邀请码", "欢迎关注ScalaChan-博客, 您的邀请码是:" + inviteCode + ",请在一小时内使用.\n如果未订阅该邮件,请忽略")
      None
    } catch {
      case e: Throwable => Some(e.toString)
    })
  }

  //http://stackoverflow.com/a/32445372/4887726
  private val emailRegex = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r
  def checkFormat(e: String): Boolean = e match {
    case null                                           => false
    case e if e.trim.isEmpty                            => false
    case e if emailRegex.findFirstMatchIn(e).isDefined  => true
    case _                                              => false
  }
}
