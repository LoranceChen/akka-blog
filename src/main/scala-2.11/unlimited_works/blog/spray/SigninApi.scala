package unlimited_works.blog.spray

import java.util.concurrent.TimeUnit

import akka.actor.ActorRef
import akka.util.Timeout
import spray.http.HttpRequest
import unlimited_works.blog.util.{SessionMultiDomain, Helpers, Config}

import scala.concurrent.ExecutionContext
import akka.pattern._
import unlimited_works.blog.akka.Signin
/**
  *
  */
trait SigninApi {
  def createSignin(): ActorRef

  implicit def executionContext: ExecutionContext
  implicit def requestTimeout: Timeout

  lazy val signinActor = createSignin()

  def loginRecord(req: HttpRequest) = {
    Helpers.toFutureOpt( req.cookies.find(_.name == Config.CookieSession.GOD_SESSION) map { godS =>
      val signinActor2 = signinActor
      signinActor.
      //todo why use context implicit Timeout throw java.lang.NullPointerException
        ask(Signin.GetLoginRecorder(godS.content))(Timeout(7, TimeUnit.SECONDS)).
        mapTo[Option[Config.RememberMeData]]
    }).map(_.flatten)
  }

  def rememberMe(req: HttpRequest, act: String, pwd: String): Unit = {
    req.cookies.find(_.name == Config.CookieSession.GOD_SESSION) foreach { godS =>
      signinActor ! Signin.RememberMe(godS.content, act, pwd)
    }
  }
}
