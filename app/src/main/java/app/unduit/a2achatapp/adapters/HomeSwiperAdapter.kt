package app.unduit.a2achatapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.ItemCardSwipeBinding
import app.unduit.a2achatapp.helpers.DateHelper
import app.unduit.a2achatapp.helpers.addComma

import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.PropertyData
import com.bumptech.glide.Glide


class HomeSwiperAdapter(
    val context: Context,
    private val listener: AdapterListener,
    private val homeSwipeList: ArrayList<PropertyData>
) :
    RecyclerView.Adapter<HomeSwiperAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCardSwipeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCardSwipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return homeSwipeList.size // Return the number of items in your data list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val propertyItem = homeSwipeList[position]

        holder.binding.propertyType.text = propertyItem.property_type
        val price = if(propertyItem.sp.isNotEmpty()) addComma(propertyItem.sp) + " AED" else addComma(propertyItem.rented_for) + " AED/month"
        holder.binding.propertyPrice.text = price
        holder.binding.userName.text = propertyItem.user_name
        holder.binding.itemLocation.text = propertyItem.area_community
        holder.binding.bedrooms.text = propertyItem.bedrooms
        holder.binding.bathroom.text = propertyItem.bathrooms
        holder.binding.propertyTime.text = DateHelper.convertTimestampToTimeAgo(propertyItem.created_date.toLong())

        holder.binding.sqFeet.text = "${propertyItem.area_size}sqft"

        if(propertyItem.property_images?.isNotEmpty() == true) {
            Glide.with(holder.binding.itemImage).load(propertyItem.property_images?.get(0)).into(holder.binding.itemImage)
        }


            Glide.with(holder.binding.userImage).load(propertyItem.user_picture).error(R.drawable.ic_profile_pic_placeholder).into(holder.binding.userImage)


        // Set a click listener on the root view if needed
        holder.binding.viewDetail.setOnClickListener {
            listener.onAdapterItemClicked("view_detail",position)
        }

        // Set a click listener on the root view if needed
        holder.binding.root.setOnClickListener {
            listener.onAdapterItemClicked("",position)
        }

        // Execute pending bindings to update the UI
        holder.binding.executePendingBindings()
    }

}
