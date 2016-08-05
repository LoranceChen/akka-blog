package unlimited_works.blog.spary

import akka.actor.{ActorRef, ActorSystem}
import spray.http.HttpHeader
import spray.http.HttpHeaders.RawHeader
import spray.httpx.unmarshalling.FormDataUnmarshallers
import spray.routing.directives.RespondWithDirectives

import akka.util.Timeout
import net.liftweb.json.Formats
import spray.routing.Route
import spray.routing.Directives._
import spray.http.StatusCodes

trait RestApi
  extends RestRoutes {
  implicit val requestTimeout = timeout
  implicit def executionContext = system.dispatcher
  implicit def liftJsonFormats: Formats = net.liftweb.json.DefaultFormats

  implicit val system: ActorSystem
  implicit val timeout: Timeout

  def createSignin: ActorRef = ???
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

  val supportDomains = List(
    "http://akka.scalachan.com:4000",
    "https://akka.scalachan.com:4000",
    "http://www.scalachan.com:4000",
    "https://www.scalachan.com:4000"
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
          path("signin.json") {
            post {
              formFields('account, 'password) { (account, password) =>
                println("(account, password) - " + account + " " + password)
                complete(s"The account is '$account' and the account is '${password}")
              }
            }
          }
        }(raw)
      case  None =>
        complete(StatusCodes.Forbidden)(raw)
    }
  }
}
