package unlimited_works.blog.spray

import java.util.concurrent.TimeUnit

import akka.actor.ActorRef
import akka.util.Timeout
import spray.http.HttpRequest
import unlimited_works.blog.util.{Helpers, Config}

import scala.concurrent.ExecutionContext
import akka.pattern._
import unlimited_works.blog.akka.Signin
import unlimited_works.blog.akka
/**
  *
  */
trait SigninApi {
  def createSignin(): ActorRef

  implicit def executionContext: ExecutionContext
  implicit def requestTimeout: Timeout

  lazy val signinActor = createSignin()

  val myTimeout = akka.timeout

  def loginRecord(req: HttpRequest) = {
    Helpers.toFutureOpt( req.cookies.find(_.name == Config.CookieSession.GOD_SESSION) map { godS =>
      val signinActor2 = signinActor
      signinActor.
      //todo why use context implicit Timeout throw java.lang.NullPointerException
        ask(Signin.GetLoginRecorder(godS.content))(myTimeout).
        mapTo[Option[Config.RememberMeData]]
    }).map(_.flatten)
  }

  def rememberMe(req: HttpRequest, act: String, pwd: String): Unit = {
    req.cookies.find(_.name == Config.CookieSession.GOD_SESSION) foreach { godS =>
      signinActor ! Signin.RememberMe(godS.content, act, pwd)
    }
  }

  def registerMail(visitorS: String, toMail: String) = {
    signinActor.ask(Signin.GetRegisterMail(visitorS, toMail))(myTimeout).mapTo[Option[String]]
  }

  def checkMailNotUsed(mail: String) = {
    signinActor.ask(Signin.CheckMailNotUsed(mail))(myTimeout).mapTo[Boolean]
  }
}
