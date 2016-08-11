package unlimited_works.blog.akka

import akka.actor.{Props, Actor}
import unlimited_works.blog.akka.Signin.{RememberMe, GetLoginRecorder}
import unlimited_works.blog.util.{Config, SessionMultiDomain}
import net.liftweb.json._

/**
  * 1. remember user account-password
  */
class Signin extends Actor {
  implicit val default = DefaultFormats
//  import context
  def receive = {
    case GetLoginRecorder(godSession) =>
      val data = SessionMultiDomain.get(godSession, Config.CookieSession.REMEMBER_ME).map {info =>
        println("info - " + info)
        parse(info).extract[Config.RememberMeData]
      }
      sender() ! data
    case RememberMe(godS, act, pwd) =>
      val rememberMeDataJStr = compactRender(Extraction.decompose(Config.RememberMeData(act, pwd)))
      SessionMultiDomain.put(godS, Config.CookieSession.REMEMBER_ME -> rememberMeDataJStr)
  }
}

object Signin {
  def props = Props(classOf[Signin])

  //remember me
  case class GetLoginRecorder(godSession: String)
  case class RememberMe(godSession: String, act: String, pwd: String)
}
