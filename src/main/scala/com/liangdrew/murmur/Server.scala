package com.liangdrew.murmur

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpMethods._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
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

    val serverSource = Http().bind(interface = hostname, port = port)

    val requestHandler: HttpRequest => HttpResponse = {
        case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
            HttpResponse(entity = HttpEntity(
                ContentTypes.`text/html(UTF-8)`, "<html><body>Hello world!</body></html>")
            )
        case HttpRequest(GET, Uri.Path("/ping"), _, _, _) =>
            HttpResponse(entity = "PONG!")
        case HttpRequest(GET, Uri.Path("/crash"), _, _, _) =>
            HttpResponse(entity = "BOOM!")
        case r: HttpRequest =>
            // Drain incoming HTTP entity stream
            r.discardEntityBytes()
            HttpResponse(404, entity = "Unknown resource!")
    }

    val bindingFuture: Future[ServerBinding] =
        serverSource.to(Sink.foreach { connection =>
            println("Accepted new connection from " + connection.remoteAddress)

            connection handleWithSyncHandler requestHandler
        }).run()

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
