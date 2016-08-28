package com.liangdrew.murmur

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.liangdrew.murmur.services.TestService
import com.typesafe.config.{Config, ConfigFactory}

import scala.io.StdIn

object Server extends App {

    // Actor needs ActorSystem to operate
    implicit val system = ActorSystem("murmur")
    // Flow needs ActorMaterializer to stream data
    implicit val materializer = ActorMaterializer
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val systemConfigs: Config = ConfigFactory.load()
    val hostname = systemConfigs.getString("murmur.http.hostname")
    val port = systemConfigs.getInt("murmur.http.port")

    val route = TestService.route

    val bindingFuture = Http().bindAndHandle(route, hostname, port)
    println(s"Server is up at http://$hostname:$port\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return

    bindingFuture
        .flatMap(_.unbind()) // Trigger unbinding from the port
        .onComplete(_ => system.terminate()) // Shut down when done
}
