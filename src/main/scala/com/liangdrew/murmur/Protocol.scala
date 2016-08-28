package com.liangdrew.murmur

import akka.actor.ActorRef

final case class ChatMessage(username: String, message: String)

object SystemMessage {
    def apply(message: String) = ChatMessage("System", message)
}

sealed trait ChatEvent

final case class JoinRoom(username: String, senderRef: ActorRef) extends ChatEvent
final case class LeaveRoom(username: String) extends ChatEvent
final case class IncomingMessage(username: String, message: String) extends ChatEvent
