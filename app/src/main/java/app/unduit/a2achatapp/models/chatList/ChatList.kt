package app.unduit.a2achatapp.models.chatList

import app.unduit.a2achatapp.models.chatList.Chat

data class ChatList(
    val chat: List<Chat>,
    val sender_id: String,
    val last_seen:Boolean=false
)