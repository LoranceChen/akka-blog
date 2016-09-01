package unlimited_works.blog.akka

import akka.actor.{ActorRef, Props, Actor}
import unlimited_works.blog.akka.Signin._
import unlimited_works.blog.util.mail.Email

//{GetRegisterMail, RememberMe, GetLoginRecorder}
import unlimited_works.blog.util.{BlogRedis, Config, SessionMultiDomain}
import net.liftweb.json._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern._
import scredis._

import scala.concurrent.Future
/**
  * 1. remember user account-password
  */
class Signin(dbConnector: ActorRef) extends Actor {
  implicit val default = DefaultFormats
//  import context
  def receive = {
    case GetLoginRecorder(godSession) =>
      SessionMultiDomain.get(godSession, Config.CookieSession.REMEMBER_ME).map {info =>
          info.map(parse(_).extract[Config.RememberMeData])
      }.pipeTo(sender())

    case RememberMe(godS, act, pwd) =>
      val rememberMeDataJStr = compactRender(Extraction.decompose(Config.RememberMeData(act, pwd)))
      SessionMultiDomain.put(godS, Config.CookieSession.REMEMBER_ME -> rememberMeDataJStr)
    case GetRegisterMail(visitorS, toMail) =>
      val code = ((Math.random() * 9 + 1) * 100000).toInt.toString
      //todo add cache!! 每分钟只能发送一次请求,每天最多发送5次 - 要加验证码才有意义
      RegisterMailCache.addRegsiterMail(visitorS, toMail, code).flatMap{
        case true =>
          Email.sendForRegister(code, toMail)
        case false => Future.successful(Some("visitor session set to redis error")) //todo log the fatal info
      }.pipeTo(sender())
    case RetrieveRegisterMail(visitorS) =>
      RegisterMailCache.getMailNumber(visitorS).pipeTo(sender())
    case e @ CheckMailNotUsed(mail) =>
      dbConnector.ask(e)(timeout).pipeTo(sender())
  }
}

object Signin {
  def props(dbConnector: ActorRef) = Props(classOf[Signin], dbConnector)

  //remember me
  case class GetLoginRecorder(godSession: String)
  case class RememberMe(godSession: String, act: String, pwd: String)

  case class GetRegisterMail(visitorS: String, toMail: String)
  case class RetrieveRegisterMail(visitorS: String)


  type CheckMailNotUsed = DbConnector.CheckMailNotUsed
  val CheckMailNotUsed = DbConnector.CheckMailNotUsed
}

//play also use the key-value, should as a jar to dependency.
object RegisterMailCache {
  val REGISTER_MAIL = "register_mail"

  val redisClient = BlogRedis.client
  val cacheSeconds = 60 * 30 //a hour
  def getKeyBySession(visitorSession: String) = visitorSession + "__" + REGISTER_MAIL

  def addRegsiterMail(visitorSession: String, mail: String, code: String): Future[Boolean] ={
    val key = getKeyBySession(visitorSession)
    redisClient.set(key, code + ";" + mail).flatMap { _ =>
      redisClient.expire(key, cacheSeconds)
    }
  }

  def getMailNumber(visitorSession: String) = {
    redisClient.get(getKeyBySession(visitorSession))
  }
}