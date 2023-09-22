package app.unduit.a2achatapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.ItemPropertyListBinding
import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.helpers.DateHelper
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.PropertyData
import com.bumptech.glide.Glide

class PropertyListAdapter(
    val context: Context,
    private val listener: AdapterListener,
    private val propertyList: ArrayList<PropertyData>,
    private var propertyType:String
) :
    RecyclerView.Adapter<PropertyListAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: ItemPropertyListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPropertyListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return propertyList.size // Return the number of items in your data list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val propertyItem = propertyList[position]

        // Bind your data to the layout using data binding
        Log.e("Tag1234", "property Type :: $propertyType")
        if(Const.PropertyType=="favourite"){
            holder.binding.btnFav.visibility=View.VISIBLE
            holder.binding.clContact.visibility=View.GONE
            holder.binding.pendingBtn.visibility=View.GONE

            holder.binding.propertyTime.text = DateHelper.convertTimestampToTimeAgo(propertyItem.created_date.toLong())
            holder.binding.propertyType.text = propertyItem.property_type
            holder.binding.propertyPrice.text = "${propertyItem.sp} AED"
            holder.binding.itemLocation.text = propertyItem.area_community
            holder.binding.bedrooms.text = propertyItem.bedrooms
            holder.binding.bathroom.text = propertyItem.bathrooms
            holder.binding.sqFeet.text = "${propertyItem.area_size}sqft"
            holder.binding.userName.text = "${propertyItem.user_name}"


        }
        else if (Const.PropertyType=="request"){
            holder.binding.btnFav.visibility=View.GONE
            holder.binding.clContact.visibility=View.GONE
            holder.binding.pendingBtn.visibility=View.VISIBLE
            holder.binding.propertyType.text = propertyItem.property_type
            holder.binding.propertyPrice.text = "${propertyItem.sp} AED"
            holder.binding.itemLocation.text = propertyItem.area_community
            holder.binding.bedrooms.text = propertyItem.bedrooms
            holder.binding.bathroom.text = propertyItem.bathrooms
            holder.binding.sqFeet.text = "${propertyItem.area_size}sqft"
            holder.binding.userName.text = "${propertyItem.user_name}"
            holder.binding.propertyTime.text = DateHelper.convertTimestampToTimeAgo(propertyItem.created_date.toLong())
            Glide.with(holder.binding.userImage).load(propertyItem.user_picture).error(R.drawable.ic_profile_pic_placeholder).into(holder.binding.userImage)


            holder.binding.clContact.visibility=View.GONE
            holder.binding.pendingBtn.visibility=View.VISIBLE
        }
        else if(Const.PropertyType=="match"){
            holder.binding.btnFav.visibility=View.GONE
            holder.binding.clContact.visibility=View.VISIBLE
            holder.binding.pendingBtn.visibility=View.GONE
            holder.binding.propertyType.text = propertyItem.property_type
            holder.binding.propertyPrice.text = "${propertyItem.sp} AED"
            holder.binding.itemLocation.text = propertyItem.area_community
            holder.binding.bedrooms.text = propertyItem.bedrooms
            holder.binding.bathroom.text = propertyItem.bathrooms
            holder.binding.sqFeet.text = "${propertyItem.area_size}sqft"

            holder.binding.propertyTime.text = DateHelper.convertTimestampToTimeAgo(propertyItem.created_date.toLong())

            if(Const.userId==propertyItem.user_id){
                holder.binding.userName.text = "${propertyItem.sender_name}"
                Glide.with(holder.binding.userImage).load(propertyItem.sender_image).error(R.drawable.ic_profile_pic_placeholder).into(holder.binding.userImage)



            }else {
                holder.binding.userName.text = "${propertyItem.user_name}"
                Glide.with(holder.binding.userImage).load(propertyItem.user_picture).error(R.drawable.ic_profile_pic_placeholder).into(holder.binding.userImage)

            }

        }



        if(propertyItem.property_images?.isNotEmpty() == true) {
            Glide.with(holder.binding.itemPropertyImage).load(propertyItem.property_images?.get(0)).into(holder.binding.itemPropertyImage)
        }
        // Set a click listener on the root view if needed
        holder.binding.root.setOnClickListener {
                listener.onAdapterItemClicked(Const.PropertyType, position)


        }
        holder.binding.btnPhone.setOnClickListener {
                listener.onAdapterItemClicked("phone_call", position)


        }
        holder.binding.btnWhatsapp.setOnClickListener {
                listener.onAdapterItemClicked("whatsapp", position)


        }
        holder.binding.btnChat.setOnClickListener {
                listener.onAdapterItemClicked("chat", position)


        }

        holder.binding.btnFav.setOnClickListener {
//            listener.onItemClick(propertyItem)
            listener.onAdapterItemClicked("un_favourite", position)
        }

        // Execute pending bindings to update the UI
        holder.binding.executePendingBindings()
    }


        fun setStatus(type:String) {

            propertyType=type

        }

}
