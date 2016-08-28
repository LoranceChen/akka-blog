package unlimited_works.blog.util.mail

import java.util.concurrent.ForkJoinPool

import unlimited_works.blog.util.Config

import scala.concurrent.{ExecutionContext, Future, blocking}

/**
  *
  */
object Email {
  val pool = new ForkJoinPool(5)
  implicit val ex = ExecutionContext.fromExecutor(pool)
  def sendForRegister(inviteCode: String)(implicit ec: ExecutionContext) = Future {
    blocking(try{
      val password = Config.Email.password
      val user = Config.Email.user
      JavaEmail.sendMail(user, user, password, "w68055010@qq.com", "Java Mail 测试邮件", "欢迎注册ScalaChan, 您的邀请码是" + inviteCode)
      None
    } catch {
      case e: Throwable => Some(e.toString)
    })
  }

}
