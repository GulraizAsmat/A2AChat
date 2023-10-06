package app.unduit.a2achatapp.models.chatList

data class Chat(
    val file_size: String,
    val sender_id: String,
    val sender_msg_id: String,
    val file_url: String,
    val message: String,
    val message_type: String,
    var seen_status: Boolean,
    var sender_name: String,
    val time: String,
    var sender_image: String="",
    var sender: Boolean=false,
    var user_online_status: Any?=false,
    var receiverName: String="",
    var receiverId: String="",
    var receiverImage: String="",
)