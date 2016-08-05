package unlimited_works.blog.spary

import akka.actor.ActorRef
import akka.util.Timeout

import scala.concurrent.ExecutionContext

/**
  *
  */
trait BlogApi {
  def createBlog(): ActorRef

  implicit def executionContext: ExecutionContext
  implicit def requestTimeout: Timeout

}