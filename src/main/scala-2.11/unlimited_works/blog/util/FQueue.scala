package unlimited_works.blog.util

import java.util.concurrent.ConcurrentLinkedQueue

import scala.concurrent.{Promise, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * a queue to save futures and do elem one the former completed.
  */
class FQueue {
  val lock = new AnyRef
  val queue = new ConcurrentLinkedQueue[() => Future[Any]]()

  /**
    * 1. get all exists item and do it one by one
    * 2. wait() if no elem exist
    */
  val task = new Thread {
    override def run(): Unit = {
      while(true) {
        //awake after all Future completed
        doFutureOneByOne.onComplete(_ => lock.synchronized(lock.notify()))

        //hang the thread avoid busy wait
        lock.synchronized(lock.wait())
      }
    }
  }

  task.start()

  //get queue items until queue is empty and return last called future
  def doFutureOneByOne: Future[Unit] = {
    Option(queue.poll()) match {
      case Some(f) =>
        val promise = Promise[Unit]

        f().onComplete{
          m =>
            //get next item form queue when completed
            doFutureOneByOne.onComplete{
              y =>
                //on this step means the recursive is completed and mark the future linker is completed
                promise.trySuccess(())
            }
        }

        promise.future
      case None => Future.successful(())
    }
  }


  def enQueue(futureAction: () => Future[Any]) = {
    queue.add(futureAction)

    lock.synchronized(lock.notify())
  }
}
