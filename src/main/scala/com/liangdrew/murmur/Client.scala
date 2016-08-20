package com.liangdrew.murmur

import akka.actor.{Actor, ActorRef, PoisonPill}

class Client(val username: String, server: ActorRef) extends Actor with MessageTypes {

    // Will connect to server upon creation
    server ! ConnectToServer(username)

    def receive = {
        case NewMessage(senderUsername, message) => {
            println(f"[$username%s's client] - $senderUsername%s: $message%s")
        }
        case SendMessage(message) => {
            server ! BroadcastMessage(message)
        }
        case Info(message) => {
            println(f"[$username%s's client] - $message%s")
        }
        case Disconnect => {
            // PoisonPill will terminate the actor when the message in the queue is received
            // whereas, with `stop`, it occurs right after the message currently being processed is completed
            self ! PoisonPill
        }
    }

}
