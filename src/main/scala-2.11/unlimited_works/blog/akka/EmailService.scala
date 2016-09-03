package unlimited_works.blog.akka

import java.util.Calendar

import akka.actor.{Props, Actor}
import unlimited_works.blog.util.algorithm.RandomCode
import unlimited_works.blog.util.mail.Email
import unlimited_works.blog.util.{FQueue, BlogRedis, Helpers}
import unlimited_works.blog.akka.EmailService._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import akka.pattern._
import scredis.serialization.Implicits._

/**
  * put the actor to a single machine because it will blocking always (for security write data)
  */
class EmailService extends Actor {
  //need a queue to save futures and deal one by one
  val fQueue = new FQueue()

  def receive = {
    case GetRegisterByMail(visitorS, toMail) =>
      val theSender = sender()
      val myF = () => RegisterMailCache.getRegisterMail(Some(visitorS), toMail).flatMap {
        //success <- t._3 < 5 or none & t._2 none
        case (_, None, dayLimit) =>
          dayLimit match {
            case Some(x) if x >= 5 => Future.successful(Some("邮件发送请求超过当天最大限制"))
            case allowed =>
              val newCount = allowed.map(_ + 1).getOrElse(1)
              val code = RandomCode.id(8)

              RegisterMailCache.addRegisterMail(visitorS, toMail, code, newCount).flatMap{
                case true =>
                  Email.sendForRegister(code, toMail)
                case false => Future.successful(Some("visitor session set to redis error")) //todo log the fatal info
              }
          }
        //fail
        case _ => Future.successful(Some("1分钟不能重复发送"))
      }.pipeTo(theSender)

      fQueue.enQueue(myF)
  }
}

object EmailService {
  def props = Props(classOf[EmailService])

  case class GetRegisterByMail(visitorS: String, toMail: String)

}

//play also use the key-value, should as a jar to dependency.
object RegisterMailCache {
  val REGISTER_MAIL = "register_mail"

  val redisClient = BlogRedis.client
  val cacheSeconds = 60 * 60 * 24//24 hour
  def getKeyBySession(visitorSession: String) = visitorSession + "__" + REGISTER_MAIL
  def getKeyForSpareLimitKey(mail: String) = mail + "__spare_limit" ////limit register time
  def getKeyForDayLimitKey(mail: String) = mail + "__day_limit" ////limit register time
  val keyForSpareLimitKeyTime = 60 //limit 60s

  //the day of 24:00 timestamp
  def keyForDayLimitKeyTime = {
    val calendar = Calendar.getInstance
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    calendar.add(Calendar.DATE, 1)
    calendar.getTimeInMillis
  }

  /**
    * 添加3组cache:
    *   'session__register_mail -> 'code;'mail -> 24 h
    *   'mail__spare_limit -> true -> 1 min
    *   'mail__day_limit -> 1-5 -> at beijing 00:00
    */
  def addRegisterMail(visitorSession: String, mail: String, code: String, dayCount: Int): Future[Boolean] = {
    val key = getKeyBySession(visitorSession)
    val keyForSpareLimitKey = getKeyForSpareLimitKey(mail)
    val keyForDayLimitKey = getKeyForDayLimitKey(mail)

    redisClient.set(key, code + ";" + mail).flatMap { _ =>
      val expireT1 = redisClient.expire(key, cacheSeconds)
      val timeSapreLimit = redisClient.set(keyForSpareLimitKey, true).flatMap { _ =>
        redisClient.expire(keyForSpareLimitKey, keyForSpareLimitKeyTime)
      }

      val timeDayLimit = redisClient.set(keyForDayLimitKey, dayCount).flatMap { _ =>
        redisClient.expireAt(keyForDayLimitKey, keyForDayLimitKeyTime)
      }

      for{
        e1 <- expireT1
        e2 <- timeSapreLimit
        e3 <- timeDayLimit
      } yield {
        e1 && e2 && e3
      }
    }
  }

  /**
    * 获取3组cache的数据
    *
    * @param mail
    */
  def getRegisterMail(visitorSession: Option[String], mail: String) = {
    val codeAndMail = Helpers.toFutureOpt(visitorSession.map(vs => redisClient.get[String](getKeyBySession(vs)))).map(_.flatten)
    val spareLimit: Future[Option[Boolean]] = redisClient.get[Boolean](getKeyForSpareLimitKey(mail))
    val dayLimit: Future[Option[Int]] = redisClient.get[Int](getKeyForDayLimitKey(mail))
    for{
      c <- codeAndMail
      s <- spareLimit
      d <- dayLimit
    } yield {
      (c,s,d)
    }
  }

  def getMailNumber(visitorSession: String) = {
    redisClient.get[String](getKeyBySession(visitorSession))
  }
}
