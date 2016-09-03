package com.liangdrew.murmur.chat

import akka.actor.ActorRef

object MessageTypes {

    sealed trait ChatEvent

    final case class JoinRoom(username: String, senderRef: ActorRef) extends ChatEvent
    final case class LeaveRoom(username: String) extends ChatEvent
    final case class IncomingMessage(username: String, message: String) extends ChatEvent

    object SystemMessage {
        def apply(message: String) = IncomingMessage("System", message)
    }

}