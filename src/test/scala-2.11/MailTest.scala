import scala.concurrent.ExecutionContext.Implicits.global
/**
  *
  */
object MailTest extends App {
  unlimited_works.blog.util.mail.Email.sendForRegister("123TEST", "w68055010@qq.com").foreach(println)
  Thread.currentThread().join()
}
