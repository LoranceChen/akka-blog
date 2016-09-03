package unlimited_works.blog.akka

import akka.actor.{ActorRef, Props, Actor}
import unlimited_works.blog.akka.Signin._
import unlimited_works.blog.util.{Config, SessionMultiDomain}
import net.liftweb.json._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern._

/**
  * remember user account-password
  */
class Signin(dbConnector: ActorRef, mailService: ActorRef) extends Actor {
  implicit val default = DefaultFormats

  def receive = {
    case GetLoginRecorder(godSession) =>
      SessionMultiDomain.get(godSession, Config.CookieSession.REMEMBER_ME).map {info =>
          info.map(parse(_).extract[Config.RememberMeData])
      }.pipeTo(sender())
    case RememberMe(godS, act, pwd) =>
      val rememberMeDataJStr = compactRender(Extraction.decompose(Config.RememberMeData(act, pwd)))
      SessionMultiDomain.put(godS, Config.CookieSession.REMEMBER_ME -> rememberMeDataJStr)
    case e @ GetRegisterByMail(visitorS, toMail) =>
      mailService.forward(e)
    case RetrieveRegisterMail(visitorS) =>
      RegisterMailCache.getMailNumber(visitorS).pipeTo(sender())
    case e @ CheckMailNotUsed(mail) =>
      dbConnector.ask(e)(timeout).pipeTo(sender())
  }
}

object Signin {
  def props(dbConnector: ActorRef,mailService : ActorRef) = Props(classOf[Signin], dbConnector,mailService)

  //remember me
  case class GetLoginRecorder(godSession: String)
  case class RememberMe(godSession: String, act: String, pwd: String)

  type GetRegisterByMail = EmailService.GetRegisterByMail
  val GetRegisterByMail = EmailService.GetRegisterByMail

  case class RetrieveRegisterMail(visitorS: String)


  type CheckMailNotUsed = DbConnector.CheckMailNotUsed
  val CheckMailNotUsed = DbConnector.CheckMailNotUsed
}
