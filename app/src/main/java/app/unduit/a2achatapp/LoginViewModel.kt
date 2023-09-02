package app.unduit.a2achatapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class LoginViewModel : ViewModel() {

//    private var auth: FirebaseAuth = Firebase.auth
//
//    var apiListener: ApiListener? = null
//
//    val progressDialogBool = MutableLiveData<Boolean>()
//
//    init {
//        progressDialogBool.value = false
//    }
//
//    fun validateData(email: String, password: String) {
//        if(email.isEmpty()) {
//            apiListener?.onFailure("Please enter email")
//        } else if(password.isEmpty()) {
//            apiListener?.onFailure("Please enter password")
//        } else {
//            loginUser(email, password)
//        }
//    }
//
//    private fun loginUser(email: String, password: String){
//
//
//
//    }
}