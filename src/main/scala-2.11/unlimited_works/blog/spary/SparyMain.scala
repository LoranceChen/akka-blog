package unlimited_works.blog.spary

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.{ConfigFactory, Config}
import spray.http.HttpHeaders.RawHeader
import spray.routing

import scala.concurrent.Future
import spray.routing.{HttpService, SimpleRoutingApp}

/**
  *
  */
//object SparyMain extends App with RequestTimeout{
//
//  val config = ConfigFactory.load()
//  val host = config.getString("http.host") // Gets the host and a port from the configuration
//  val port = config.getInt("http.port")
//
//  implicit val system = ActorSystem()
//  implicit val ec = system.dispatcher  //bindAndHandle requires an implicit ExecutionContext
//
//  implicit val materializer = ActorMaterializer()
//  val api = new RestApi(system, requestTimeout(config)).routes
//
//  val bindingFuture: Future[ServerBinding] =
//    Http().bindAndHandle(api, host, port) //Starts the HTTP server
//
//  val log =  Logging(system.eventStream, "go-ticks")
//  bindingFuture.map { serverBinding =>
//    log.info(s"RestApi bound to ${serverBinding.localAddress} ")
//  }.onFailure {
//    case ex: Exception =>
//      log.error(ex, "Failed to bind to {}:{}!", host, port)
//      system.terminate()
//  }
//}

trait RequestTimeout {
  import scala.concurrent.duration._
  def requestTimeout(config: Config): Timeout = { //<co id="ch02_timeout_spray_can"/>
  val t = config.getString("akka.http.server.request-timeout")
    val d = Duration(t)
    FiniteDuration(d.length, d.unit)
  }
}

object Main extends App with routing.SimpleRoutingApp with RestApi with RequestTimeout{
  implicit val system = ActorSystem("my-system")
  val config = ConfigFactory.load()

  val host = config.getString("http.host") // Gets the host and a port from the configuration
  val port = config.getInt("http.port")
  override implicit val timeout: Timeout = requestTimeout(config)

  startServer(interface = host, port = port) {
    routes
  }
}

trait WithCrosService extends HttpService {
//  def fcross(origin: String) = {
//    respondWithHeaders(RawHeader("Access-Control-Allow-Origin", origin),
//      RawHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS"),
//      RawHeader("Access-Control-Allow-Headers", "X-Requested-With,Cache-Control,Progma,Origin,Authorization,Content-Type"),
//      RawHeader("Access-Control-Max-Age", "86400"),
//      RawHeader("Content-Type", "application/json"))
//  }
//  def crossDomain = fcross("*")
}