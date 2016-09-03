package com.liangdrew.murmur.chat

import akka.actor.Actor.Receive
import akka.actor._
import com.liangdrew.murmur.chat.MessageTypes._

class ChatRoomActor extends Actor {

    println("Instantiated chatRoomActor")

    // ActorRef here is a websocket endpoint for connected client
    var members: Map[String, ActorRef] = Map.empty[String, ActorRef]

    def receive = {
        case JoinRoom(username, senderRef) => {
            println("Receive JoinRoom message")
            members += username -> senderRef
            broadcast(SystemMessage(s"$username just joined the room"))
            println(s"$username just joined the room")
        }
        case LeaveRoom(username) => {
            members -= username
            broadcast(SystemMessage(s"$username just left the room"))
            println(s"$username just left the room")
        }
        case IncomingMessage(username, senderRef) => {
            broadcast(IncomingMessage(username, senderRef))
        }
    }

    def broadcast(message: IncomingMessage): Unit = {
        members.values.foreach(_ ! message)
    }
}

object ChatRoomActor {

    def props(): Props = Props(new ChatRoomActor())

    final case class JoinRoom(username: String, senderRef: ActorRef)
    final case class LeaveRoom(username: String)
}
