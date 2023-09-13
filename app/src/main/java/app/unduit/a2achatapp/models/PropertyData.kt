package app.unduit.a2achatapp.models

import android.os.Parcelable
import app.unduit.a2achatapp.helpers.DateHelper
import kotlinx.parcelize.Parcelize

@Parcelize
class PropertyData(

    var uid: String = "",
    var user_id: String? = "",
    var name: String = "",
    var image: Int = 0,
    var selected: Boolean = false,
    var bhk: String = "",
    var price: String = "",
    var sqft: String = "",
    var location: String = "",
    var purpose: String = "",
    var purpose_type: String = "",
    var property_type: String = "",
    var area_community: String = "",
    var project: String = "",
    var bedrooms: String = "",
    var bathrooms: String = "",
    var development_status: String = "",
    var area_size: String = "",
    var maidroom: Boolean = false,
    var balcony: Boolean = false,
    var occupancy: String = "",
    var furnishing: String = "",
    var service_charge: String = "",
    var floor: String = "",
    var payment_during_construction: String = "",
    var payment_on_handover: String = "",
    var payment_post_handover: String = "",
    var handover_year: String = "",
    var op: String = "",
    var sp: String = "",
    var roi: String = "",
    var negotiation: String = "",
    var property_title: String = "",
    var property_images: List<String>? = null,
    var property_status :String=""
//    var created_date : String=""

): Parcelable