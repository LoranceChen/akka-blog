package unlimited_works.blog.util

import java.security.MessageDigest

import scala.concurrent.{ExecutionContext, Future}

/**
  *
  */
trait MD5Helper {
  def stringTo32ByteMD5(raw: String): String = {
    val md5Bytes = MessageDigest.getInstance("MD5").
      digest(raw.getBytes)
    val hexValue = new StringBuffer
    for( c <- md5Bytes ) {
      val value = c.toInt & 0xff
      if (value < 16) hexValue.append("0")
      hexValue.append(Integer.toHexString(value))
    }
    hexValue.toString
  }
}

trait FutureHelper {
  //Option[Future[]] to Future[Option[T]]
  implicit def toFutureOpt[T](optFur: Option[Future[T]])(implicit ec: ExecutionContext): Future[Option[T]] = {
    optFur match {
      case None => Future.successful(None)
      case Some(fur) => fur.map(t =>
        Some(t)
      )
    }
  }
}
object Helpers extends MD5Helper with FutureHelper
