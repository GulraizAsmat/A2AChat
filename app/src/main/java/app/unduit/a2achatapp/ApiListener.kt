package app.unduit.a2achatapp

interface ApiListener {

    fun onStarted()
    fun onSuccess()
    fun onFailure(message: String?)

}