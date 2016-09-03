package com.liangdrew.murmur.chat

import akka.actor._
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives
import akka.serialization._
import akka.stream.OverflowStrategy
import akka.stream.scaladsl._
import akka.stream.Materializer
import com.liangdrew.murmur.chat.MessageTypes._

import upickle.default._

class ChatRoom(roomID: Int)(implicit system: ActorSystem) extends Directives {

    println("Creating new chatRoomActor")
    private val chatRoomActor = system.actorOf(Props[ChatRoomActor])

    // Subscribe chatRoomActor to flow stream
    // When stream disconnects (user logs off), leaveRoom message is sent
    def chatActorSink(username: String) = Sink.actorRef[ChatEvent](chatRoomActor, LeaveRoom(username))

    // Message handler
    def websocketFlow(username: String): Flow[Message, Message, Any] = {
        Flow[Message]
            .collect {
                case TextMessage.Strict(message) =>
                    message
            }
            .via(messagingFlow(username))
            // Construct the flow
            .map {
                case message: IncomingMessage =>
                    // Format outgoing message into JSON
                    TextMessage.Strict(write(message))
            }
    }

    // Pipeline that will take input from client, process, and send back to client
    def messagingFlow(username: String): Flow[String, IncomingMessage, Any] = {

        val in = Flow[String]
            .map(IncomingMessage(username, _))
            .to(chatActorSink(username))

        println("About to send JoinRoom message to chatRoomActor")
        val out = Source
            .actorRef[IncomingMessage](1, OverflowStrategy.fail)
            .mapMaterializedValue(chatRoomActor ! JoinRoom(username, _))
        println("Finish sending JoinRoom message to chatRoomActor")

        Flow.fromSinkAndSource(in, out)
    }

    def sendMessage(message: IncomingMessage): Unit = chatRoomActor ! message

    private def joinRoom(username: String, senderRef: ActorRef): Unit = chatRoomActor ! JoinRoom(username, senderRef)

}

object ChatRoom {
    def apply(roomID: Int)(implicit system: ActorSystem) = new ChatRoom(roomID)
}


