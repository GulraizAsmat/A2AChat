package app.unduit.a2achatapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.ItemNotificationBinding
import app.unduit.a2achatapp.databinding.ItemPropertyListBinding
import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.helpers.DateHelper
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.PropertyData
import com.bumptech.glide.Glide

class NotificationListAdapter(
    val context: Context,
    private val listener: AdapterListener,
    private val notificationList: ArrayList<PropertyData>,
) :
    RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return notificationList.size // Return the number of items in your data list
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val propertyItem = notificationList[position]

        // Bind your data to the layout using data binding

//        holder.binding.name.text=propertyItem.user_name

        if(propertyItem.property_status=="matched"){
            holder.binding.btnCancel.text="Matched"
            holder.binding.btnCancel.setTextColor(ContextCompat.getColor(context,R.color.white))
            holder.binding.btnAccept.visibility=View.GONE
            holder.binding.btnCancel.setBackgroundResource(R.drawable.bg_btn_login)

        }




        holder.binding.time.text=DateHelper.convertTimestampToTimeAgo(propertyItem.created_date.toLong())



        // Create a SpannableString with the original text
        val text = "${propertyItem.sender_name} sent the request for ${propertyItem.property_type}"
        val spannableString = SpannableString(text)

        // Apply bold and custom font family to "Gulraiz"


        val boldSpan = StyleSpan(Typeface.BOLD)
        spannableString.setSpan(boldSpan, 0, propertyItem.sender_name!!.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)


        // Change color of "Apartment"
        val colorSpan = ForegroundColorSpan(context.getColor(R.color.color_purple_shade_1)) // Replace with your desired color
        val apartmentStart = text.indexOf(propertyItem.property_type.toString())
        val apartmentEnd = apartmentStart + propertyItem.property_type.length
        spannableString.setSpan(colorSpan, apartmentStart, apartmentEnd, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Set the SpannableString in the TextView
        holder.binding.name.text=spannableString




        if(propertyItem.property_images?.isNotEmpty() == true) {
            Glide.with(holder.binding.image).load(propertyItem.sender_image).error(R.drawable.ic_profile_pic_placeholder).into(holder.binding.image)
        }
        // Set a click listener on the root view if needed
        holder.binding.btnAccept.setOnClickListener {
//            listener.onItemClick(propertyItem)
            listener.onAdapterItemClicked("match_request", position)
        }

        // Execute pending bindings to update the UI
        holder.binding.executePendingBindings()
    }



}
