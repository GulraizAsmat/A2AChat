package app.unduit.a2achatapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.ItemPropertyListBinding
import app.unduit.a2achatapp.databinding.ItemRequestListBinding
import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.helpers.DateHelper
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.PropertyData
import com.bumptech.glide.Glide

class PropertyListMultipleViewAdapter(
    val context: Context,
    private val listener: AdapterListener,
    private val propertyList: ArrayList<PropertyData>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    inner class PropertyViewHolder(val binding: ItemPropertyListBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class RequestViewHolder(val binding: ItemRequestListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == 1) {
            val binding = ItemRequestListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            RequestViewHolder(binding)
        } else {
            val binding = ItemPropertyListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            PropertyViewHolder(binding)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = propertyList[position]
        return when(item.post_type) {
            "request" -> {
                1
            }

            else -> {
                2
            }
        }
    }

    override fun getItemCount(): Int {
        return propertyList.size // Return the number of items in your data list
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val propertyItem = propertyList[position]

        // Bind your data to the layout using data binding
        when(holder) {
            is PropertyViewHolder -> {
                holder.binding.clContact.visibility = View.GONE
                holder.binding.pendingBtn.visibility = View.GONE
                holder.binding.btnFav.visibility = View.GONE

                holder.binding.propertyTime.text = DateHelper.convertTimestampToTimeAgo(propertyItem.created_date.toLong())
                holder.binding.propertyType.text = propertyItem.property_type
                holder.binding.propertyPrice.text = "${propertyItem.sp} AED"
                holder.binding.itemLocation.text = propertyItem.area_community
                holder.binding.bedrooms.text = propertyItem.bedrooms
                holder.binding.bathroom.text = propertyItem.bathrooms
                holder.binding.sqFeet.text = "${propertyItem.area_size}sqft"
                holder.binding.userName.text = "${propertyItem.user_name}"

                if(propertyItem.property_images?.isNotEmpty() == true) {
                    Glide.with(holder.binding.itemPropertyImage).load(propertyItem.property_images?.get(0)).into(holder.binding.itemPropertyImage)
                }
                // Set a click listener on the root view if needed
                holder.binding.root.setOnClickListener {
                    listener.onAdapterItemClicked(Const.PropertyType, position)
                }
                Glide.with(holder.binding.userImage).load(propertyItem.user_picture).fallback(R.drawable.ic_profile_pic_placeholder).into(holder.binding.userImage)

                // Execute pending bindings to update the UI
                holder.binding.executePendingBindings()
            }

            is RequestViewHolder -> {
                holder.binding.propertyTitle.text = propertyItem.property_title
                holder.binding.propertyDescription.text = propertyItem.property_description
                holder.binding.propertyTime.text = DateHelper.convertTimestampToTimeAgo(propertyItem.created_date.toLong())
                holder.binding.userName.text = "${propertyItem.user_name}"
                Glide.with(holder.binding.userImage).load(propertyItem.user_picture).fallback(R.drawable.ic_profile_pic_placeholder).into(holder.binding.userImage)
                holder.binding.btnChat.setOnClickListener {
                    listener.onAdapterItemClicked("chat", position)
                }
            }
        }

    }

}
