package unlimited_works.blog.finatra.controller

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.finatra.request.RouteParam
import com.twitter.finatra.validation.Size
import com.twitter.util.Future
import com.twitter.finatra._
/**
  * signin, login
  */
class Login extends Controller{
  get("/index") { request: Request =>
    println(s"index -")
    //c
    response.created.html("welcome to clean blog build by akka & finatra.")
  }
  post("/signin.json") { signinReq: SigninReq =>
    //c
//    val form = request.body.asFormUrlEncoded.get
//    val account = form.get("account").map(_.head).getOrElse("")
//    val password = form.get("password").map(_.head).getOrElse("")
//
//    if (signinReq.account.isEmpty || signinReq.password.isEmpty)
////      Future(Ok(compactRender(("result" -> 400) ~ ("msg" -> "账号和密码不能为空"))))
//      FinatraObjectMapper.create().
//    else {
//      val rst = LoginModule.verifyAndGetId(account, password)
//      rst.map { x =>
//        println(s"AccountVerifyResult - $x")
//        if (x.result.nonEmpty) {
//          val key = Helpers.stringTo32ByteMD5(account)
//          val accountId = x.result.get._id.`$oid`
//          SessionMultiDomain.puts(key, Map("accountId" -> accountId))
//
//          Ok(compactRender("result" -> 200)).withCookies(Cookie(name = "GOD_SESSION", value = key, maxAge = Some(3600 * 24 * 365), domain = Some(".scalachan.com"), httpOnly = false))
//        }
//        else Ok(compactRender(("result" -> 400) ~ ("msg" -> "身份验证失败")))
//      }
//    }

  }
}

case class TweetPostRequest(
                             @Size(min = 1, max = 140) message: String)

case class TweetGetRequest(
                            @RouteParam id: String)

case class SigninReq(account: String, password: String)