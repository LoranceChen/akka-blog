package unlimited_works.blog

import java.util.concurrent.TimeUnit

import _root_.akka.util.Timeout

/**
  *
  */
package object akka {
  val timeout = Timeout(1, TimeUnit.MINUTES)
}
