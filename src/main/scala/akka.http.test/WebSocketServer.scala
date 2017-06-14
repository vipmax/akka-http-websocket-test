package akka.http.test


import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.TextMessage
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.duration._

/**
  * Created by max on 14.06.17.
  */

object WebSocketServer {

  implicit val system = ActorSystem("WebSocketServer")
  implicit val materializer = ActorMaterializer()

  def main(args: Array[String]) {

    val route =
      pathEndOrSingleSlash {
        get {
          println("connection /")
          getFromResource("index.html")
        }
      } ~
      path("ws") {
        println("ws /")
        get {
          extractUpgradeToWebSocket { upgrade =>
            complete(upgrade.handleMessagesWithSinkSource(
              Sink.ignore, Source.tick(0 second, 1 second, TextMessage("test"))
            ))
          }
        }
      }

    Http().bindAndHandle(route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/")
  }
}
