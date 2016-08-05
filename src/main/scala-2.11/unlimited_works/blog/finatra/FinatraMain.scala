package unlimited_works.blog.finatra

import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.CommonFilters
import com.twitter.finatra.http.routing.HttpRouter
import unlimited_works.blog.finatra.controller.Login

object FinatraServerMain extends FinatraServer
class FinatraServer extends HttpServer {
  override def configureHttp(router: HttpRouter): Unit = {
    router
      .filter[CommonFilters]
      .add[Login]
  }
}