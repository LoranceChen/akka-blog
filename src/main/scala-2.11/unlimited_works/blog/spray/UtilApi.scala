package unlimited_works.blog.spray

import akka.actor.ActorRef
import akka.util.Timeout

import scala.concurrent.ExecutionContext

/**
  *
  */
trait UtilApi {
  def createBlog(): ActorRef

  implicit def executionContext: ExecutionContext
  implicit def requestTimeout: Timeout

  def isSelf = {

  }
}
