package app.unduit.a2achatapp.listeners

interface ApiListener {

    fun onStarted()
    fun onSuccess()
    fun onFailure(message: String?)

}