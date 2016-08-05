package unlimited_works.blog.spary

import akka.actor.ActorRef
import akka.util.Timeout

import scala.concurrent.{Future, ExecutionContext}

/**
  *
  */
trait SigninApi {
  def createSignin(): ActorRef

  implicit def executionContext: ExecutionContext
  implicit def requestTimeout: Timeout

  def signin(account: String, password: String): Future[Option[String]] = {
    Future(Some("NON SERVICE"))
  }
}
