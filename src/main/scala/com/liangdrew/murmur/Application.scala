package com.liangdrew.murmur

import akka.actor.{ActorSystem, Props}

object Application extends App with MessageTypes {

    override def main(args: Array[String]): Unit = {

        val system = ActorSystem("System")

        val server = system.actorOf(Props[Server])

        val AndrewClient = system.actorOf(Props(new Client("Andrew", server)))
        val BorisClient = system.actorOf(Props(new Client("Boris", server)))

        AndrewClient ! SendMessage("Hey guys, want to watch a movie tonight?")
        BorisClient ! SendMessage("Yes, let's do it!")

        val TrevorClient = system.actorOf(Props(new Client("Trevor", server)))
        val NathanClient = system.actorOf(Props(new Client("Nathan", server)))

        TrevorClient ! SendMessage("Hold on, bad connection...")

        TrevorClient ! Disconnect
    }
}
