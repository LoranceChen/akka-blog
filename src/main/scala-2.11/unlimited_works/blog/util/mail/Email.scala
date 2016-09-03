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
      JavaEmail.sendMail("no-reply@scalachan.com", user, password, to, s"[ScalaChan]获取注册邀请码", """欢迎关注ScalaChan的博客社区, 您的邀请码是:<b style="color:red">""" + inviteCode + s"</b>,请在24小时内使用.如果未订阅该邮件,请忽略 ;) <br> " +
        s"""<img src="https://leanote.com/api/file/getImage?fileId=57c97630ab644135ea06caf3" alt="scalachan" title="">""")
      None
    } catch {
      case e: Throwable =>
        println("邮件发送失败 - " + e.toString)
        Some("邮件发送失败")
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
