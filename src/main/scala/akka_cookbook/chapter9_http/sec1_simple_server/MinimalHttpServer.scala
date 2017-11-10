package akka_cookbook.chapter9_http.sec1_simple_server

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.HttpApp
import akka.http.scaladsl.settings.ServerSettings
import com.typesafe.config.ConfigFactory


/**
  * @author Xiangnan Ren
  */


/**
  * Launch App first, then type
  * curl http://localhost:8088/v1/id/ALICE in shell.
  */
object MinimalHttpServer extends HttpApp {

  override protected def route =
    pathPrefix("v1") {
      path("id"/Segment) { id =>
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,
            s"<h1>Hello $id from Akka Http!</h1>"))
        } ~
          post {
            entity(as[String]) { entity =>
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,
                s"<b>Thanks $id for posting your message <i>$entity</i></b>"))
            }
          }
      }
  }
}

object MinimalHttpServerApplication extends App {
  MinimalHttpServer.startServer("0.0.0.0", 8088, ServerSettings(ConfigFactory.load))
}