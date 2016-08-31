package com.liangdrew.murmur

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.Future
import scala.io.StdIn

object Server extends App {

    // Actor needs ActorSystem to operate
    implicit val system = ActorSystem("murmur")
    // Flow needs ActorMaterializer to stream data
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val systemConfigs: Config = ConfigFactory.load()
    val hostname = systemConfigs.getString("murmur.http.hostname")
    val port = systemConfigs.getInt("murmur.http.port")

    val route =
        pathEndOrSingleSlash {
            get {
                complete("server is up")
            }
        } ~
        path("room_id" / IntNumber) { room_id =>
            parameter('name) { username =>
                handleWebSocketMessages(
                    ChatRooms
                        .findRoomElseCreateNew(room_id)
                        .websocketFlow(username))
            }
        }

    val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(route, hostname, port)

    bindingFuture.onFailure {
        case ex: Exception =>
            println(s"Failed to bind to $hostname:$port")
    }
    println(s"Server is up at http://$hostname:$port\nPress RETURN to shut down server...")
    StdIn.readLine() // let it run until user presses return

    bindingFuture
        .flatMap(_.unbind()) // Trigger unbinding from the port
        .onComplete(_ => system.terminate()) // Shut down when done
}
