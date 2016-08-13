package unlimited_works.blog.spray

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import shapeless.~>
import spray.http.{HttpCookie, HttpHeader, StatusCodes}
import spray.http.HttpHeaders.{Cookie, RawHeader}
import spray.httpx.unmarshalling.FormDataUnmarshallers
import spray.routing.directives.RespondWithDirectives

import akka.util.Timeout
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import spray.routing.{RequestContext, Route}
import spray.routing.Directives._
import unlimited_works.blog.akka.Signin
import unlimited_works.blog.util.{SessionMultiDomain, Config}
import scala.concurrent.Future

trait RestApi
  extends RestRoutes {
  implicit val timeout: Timeout

  implicit val requestTimeout = timeout
  implicit def executionContext = system.dispatcher
  implicit def liftJsonFormats: Formats = net.liftweb.json.DefaultFormats

  implicit val system: ActorSystem

  def createSignin: ActorRef = system.actorOf(Signin.props)
  def createBlog: ActorRef = ???
}

trait RestRoutes extends SigninApi with BlogApi
  with EventMarshalling with RespondWithDirectives with FormDataUnmarshallers{
  implicit def liftJsonFormats: Formats
  def routes: Route = signinRoute

  def fcross(origin: String) = {
    respondWithHeaders(RawHeader("Access-Control-Allow-Origin", origin),
      RawHeader("Access-Control-Allow-Credentials", "true"),
      RawHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS"),
      RawHeader("Access-Control-Allow-Headers", "X-Requested-With,Cache-Control,Progma,Origin,Authorization,Content-Type"),
      RawHeader("Access-Control-Max-Age", "86400"),
      RawHeader("Content-Type", "application/json"))
  }
  def crossDomain = fcross("http://akka.scalachan.com:4000")

  val supportDomains = Set(
    "http://www.scalachan.com:4000",
    "https://www.scalachan.com:4000",
    "http://www.scalachan.com",
    "https://www.scalachan.com"
  )

  def signinRoute = noop { raw =>
    val origin = raw.request.headers.find{
      case HttpHeader("origin", org) =>
        supportDomains.contains(org)
      case _ => false
    }
    origin match {
      case Some(org) =>
        fcross(org.value) {
          path("signin" / "remember-me.json"){
            post{
              formFields('account, 'password) { (account, password) =>
                println("hi!")
                onSuccess(SessionMultiDomain.getAccountId(raw.request)) {
                    case Some(_) =>
                      rememberMe(raw.request, account, password)
                      respondWithMediaType(spray.http.MediaTypes.`application/json`) {
                        complete(compactRender("result" -> 200: JObject))
                      }
                    case None =>
                      respondWithMediaType(spray.http.MediaTypes.`application/json`) {
                        complete(compactRender("result" -> 4002: JObject))
                      }
                }
              }
            }
          } ~
          path("signin" / "login-record.json"){
            get{
              val rst = loginRecord(raw.request)
              val rstTojson = rst.map{
                case None => "result" -> 400 : JObject
                case Some(load) => Extraction.decompose(load) merge ("result" -> 200 : JObject)
              }
              respondWithMediaType(spray.http.MediaTypes.`application/json`) {
                complete(rstTojson.map(compactRender))
              }
            }
          }
//
//
//          path("signin.json") {
//            post {
//              formFields('account, 'password) { (account, password) =>
//
//                // that's a good idea unwrap Future immidentilly
//                val sign = onSuccess(Future("a")) { x =>
////                  println(s"AccountVerifyResult - $x")
////                  if (x.result.nonEmpty) {
////                    val key = Helpers.stringTo32ByteMD5(account)
////                    val accountId = x.result.get._id.`$oid`
////                    SessionMultiDomain.puts(key, Map("accountId" -> accountId))
////                    respondWithHeader(Cookie(HttpCookie(name = "GOD_SESSION", content = key, maxAge = Some(3600 * 24 * 365), domain = Some(".scalachan.com"), httpOnly = false))) {
////                      respondWithMediaType(spray.http.MediaTypes.`application/json`) {
////                         complete(compactRender("result" -> 200)) //signinRsp
////                      }
////                    }
////
////                  } else {
//                    respondWithMediaType(spray.http.MediaTypes.`application/json`) {
//                      complete(compactRender((("result" -> 400): JObject) ~ ("msg" -> "身份验证失败"): JObject)) //signinRsp
//                    }
////                  }
//                }
//                sign
////                spray.httpx.ResponseTransformation
////                complete(sign)
////
////                println("(account, password) - " + account + " " + password)
////                respondWithMediaType(spray.http.MediaTypes.`application/json`) {
////                  val y  = complete(Future.successful("""{"result":200}""")) //signinRsp
////                }
////                y
//              }
//            }
//          }// ~
//          path("signin/register.json") {
//            post {
//              //Option: should check at client
//              formFields('invitationCode.?,
//                'email.?,
//                'username.?,
//                'penName.?,
//                'password.?,
//                'passwordAgain.?) { (invitationCodeOpt, emailOpt,usernameOpt, penNameOpt, passwordOpt, passwordAgainOpt) =>
//                val invitationCode = invitationCodeOpt.getOrElse("")
//                val email = emailOpt.getOrElse("")
//                val username = usernameOpt.getOrElse("")
//                val penName = penNameOpt.getOrElse("")
//                val password = passwordOpt.getOrElse("")
//                val passwordAgain = passwordAgainOpt.getOrElse("")
//
//                case class FieldValidator(key: String, value: String, validator: String => Option[String]) {
//                  def validate = validator(value.trim).map(key -> _)
//                }
//
//                //soul from Tch.He
//                val checkInput = List(
//                  FieldValidator("invitationCode", invitationCode, value => if (value.isEmpty) Some("不能为空") else None),
//                  FieldValidator("email", email, value => if (value.isEmpty) Some("邮箱不能为空") else None),
//                  FieldValidator("username", username, value => if (value.isEmpty) Some("用户名不能为空") else None),
//                  FieldValidator("penName", penName, value => if (value.isEmpty) Some("笔名不能为空") else None),
//                  FieldValidator("password", password, value => {
//                    if (value.isEmpty) Some("密码不能为空")
//                    else if (value != passwordAgain) {
//                      Some("两次密码输入不同")
//                    } else None
//                  })
//                ).foldLeft(JObject(Nil)) { (errors, fieldValidator) =>
//                  fieldValidator.validate.map { e => errors.merge(JObject(JField(e._1, JString(e._2)))) }.getOrElse(errors)
//                }
//
//                if (checkInput.values.isEmpty) {
//                  onSuccess(Account.invitationCodeUseable_?(invitationCode)) { canUse =>
//                    if (canUse) {
//                      onSuccess(Account.accountExist(email, username, penName)) {
//                        case AccountExistRsp(None, _, _) =>
//                          Account.useInvitationCode(invitationCode)
//                          Account.register(email, username, penName, password)
//                          respondWithMediaType(spray.http.MediaTypes.`application/json`) {
//                            complete("""{"result":200}""") //signinRsp
//                          }
//                        case AccountExistRsp(Some(account), _, _) =>
//                          if (account.penName == penName) {
//                            respondWithMediaType(spray.http.MediaTypes.`application/json`) {
//                              complete(compactRender((("result" -> 400):JObject) ~ ("error" -> "pen_name已存在"))) //signinRsp
//                            }
//                          } else if (account.username == username) {
//                            respondWithMediaType(spray.http.MediaTypes.`application/json`) {
//                              complete(compactRender((("result" -> 400):JObject) ~ ("error" -> "usernmae已存在"))) //signinRsp
//                            }
//                          } else if (account.email == email) {
//                            respondWithMediaType(spray.http.MediaTypes.`application/json`) {
//                              complete(compactRender((("result" -> 400):JObject) ~ ("error" -> "email已存在"))) //signinRsp
//                            }
//                          } else {
//                            respondWithMediaType(spray.http.MediaTypes.`application/json`) {
//                              complete(compactRender((("result" -> 400):JObject) ~ ("error" -> "其他错误"))) //signinRsp
//                            }
//                          }
//                      }
//                    } else {
//                      respondWithMediaType(spray.http.MediaTypes.`application/json`) {
//                        complete(compactRender((("result" -> 400): JObject) ~ ("error" -> "邀请码不可用"))) //signinRsp
//                      }
//                    }
//                  }
//                } else {
//                  respondWithMediaType(spray.http.MediaTypes.`application/json`) {
//                    complete(compactRender((("result" -> 400):JObject) ~ checkInput)) //signinRsp
//                  }
//                }
//              }
//            }
//          }
        }(raw)
      case  None =>
        complete(StatusCodes.Forbidden, "the origin not support")(raw)
    }
  }
}
