package app.unduit.a2achatapp.fcm

import app.unduit.a2achatapp.helpers.Const
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener


import org.json.JSONObject

object FcmNotificationSend {

    fun post(url: String, parameters: JSONObject, onSuccess: (JSONObject) -> Unit, onFailure: (error: ANError) -> Unit) {
        AndroidNetworking.post(url)
            .addJSONObjectBody(parameters)
            .addHeaders("Authorization", Const.AUTH_KEY)
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    onSuccess(response)
                }
                override fun onError(error: ANError) {
                    onFailure(error)
                }
            })
    }
    fun postWithoutHeader(url: String, parameters: JSONObject, onSuccess: (JSONObject) -> Unit, onFailure: (error: ANError) -> Unit) {
        AndroidNetworking.post(url)
            .addJSONObjectBody(parameters)
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    onSuccess(response)
                }
                override fun onError(error: ANError) {
                    onFailure(error)
                }
            })
    }


}