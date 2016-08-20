package com.liangdrew.murmur

import akka.actor.{Actor, ActorRef, Terminated}

class Server extends Actor with MessageTypes {

    var clients = List[(String, ActorRef)]();

    def receive = {

        case ConnectToServer(username) => {
            broadcast(Info(f"$username%s has joined the room"))
            // :: (cons, i.e. construct operator makes a new list appending A to B, where the syntax is A :: B
            clients = (username, sender) :: clients
            // context.watch will notify Server actor when client actor is killed or stopped
            context.watch(sender)
        }
        case BroadcastMessage(message) => {
            val username = getUsername(sender)
            broadcast(NewMessage(username, message))
        }
        case Terminated(client) => {
            val username = getUsername(client)
            // Remove the client from collection of clients, x._2 returns the second element of the tuple x
            clients = clients.filter(sender != _._2)
            broadcast(Info(f"$username%s has left the room"))
        }
    }

    def broadcast(message: Message) {
        // send message to all clients
        clients.foreach(client => client._2 ! message)
    }

    def getUsername(actor: ActorRef): String = {
        clients.filter(actor == _._2).head._1
    }

}

