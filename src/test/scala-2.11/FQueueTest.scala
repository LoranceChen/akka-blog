import unlimited_works.blog.util.FQueue

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * consume Future one by one
  */
object FQueueTest extends App {
  val fq = new FQueue()

  val f1 = () => Future{
    Thread.sleep(2000)
    println("f1 completed at " + System.currentTimeMillis())
  }

  val f2 = () => Future{
    Thread.sleep(2000)
    println("f2 completed at " + System.currentTimeMillis())
  }
  val f3 = () => Future{
    Thread.sleep(2000)
    println("f3 completed at " + System.currentTimeMillis())
  }
  val f4 = () => Future{
    Thread.sleep(2000)
    println("f4 completed " + System.currentTimeMillis())
  }

  fq.enQueue(f1)
  fq.enQueue(f2)
  fq.enQueue(f3)
  fq.enQueue(f4)
  println("queue size - " + fq.queue.size())

  Thread.sleep(10000)
  println("queue size - " + fq.queue.size())

  Thread.currentThread().join()
}
