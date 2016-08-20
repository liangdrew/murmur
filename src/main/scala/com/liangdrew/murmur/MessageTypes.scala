package com.liangdrew.murmur

trait MessageTypes {

    abstract class Message

    final case class SendMessage(message: String) extends Message
    final case class NewMessage(senderUsername: String, message: String) extends Message
    final case class Info(message: String) extends Message

    final case class ConnectToServer(username: String) extends Message
    final case class BroadcastMessage(message: String) extends Message

    // Use case object since value differentiation does not impact work done by actor
    final case object Disconnect extends Message

}
