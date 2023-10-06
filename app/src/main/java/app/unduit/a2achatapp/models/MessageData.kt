package app.unduit.a2achatapp.models

data class MessageData (

    var senderId: String="",
    var senderName: String="",
    var receiverId: String="",
    var created: String="",
    var msgKind: String="",
    var receiverName: String="",
    var receiverImage: String="",
    var content: String="",
    var seen: Boolean=false,
    var senderImage: String=""

        )