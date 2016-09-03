package com.liangdrew.murmur.chat

import akka.actor.ActorSystem

object ChatRooms {

    var allRooms: Map[Int, ChatRoom] = Map.empty[Int, ChatRoom]

    def findRoomElseCreateNew(room_id: Int)(implicit system: ActorSystem): ChatRoom = {
        if (allRooms.keySet.exists(_ == room_id)) {
            println(s"Room ID: $room_id exists")
            println("Existing rooms:")
            allRooms.foreach(pair => println(">>> " + pair._1))
        }
        allRooms.getOrElse(room_id, createNewRoom(room_id))
    }

    private def createNewRoom(room_id: Int)(implicit system: ActorSystem): ChatRoom = {
        println(s"Creating new room ID: $room_id")
        val newRoom = ChatRoom(room_id)
        allRooms += room_id -> newRoom
        println("Existing rooms:")
        allRooms.foreach(pair => println(pair._1))
        newRoom
    }
}
