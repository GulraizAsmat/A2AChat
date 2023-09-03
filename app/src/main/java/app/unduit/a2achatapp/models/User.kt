package app.unduit.a2achatapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(

    var uid: String? = "",
    var whatsapp: String? = "",
    var certified: Boolean? = false,
    var certified_image: String? = "",
    var company: String? = "",
    var email: String? = "",
    var experience: String? = "",
    var location: String? = "",
    var name: String? = "",
    var phone: String? = "",
    var profile_image: String? = "",
    var speciality: List<String>? = null,
    var status: String? = ""

): Parcelable