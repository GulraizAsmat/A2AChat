package app.unduit.a2achatapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.ItemAgentPropertiesBinding
import app.unduit.a2achatapp.databinding.ItemAgentRequestsBinding
import app.unduit.a2achatapp.databinding.ItemPropertyFiltersBinding
import app.unduit.a2achatapp.databinding.ItemPropertyTypeBinding
import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.helpers.DateHelper
import app.unduit.a2achatapp.helpers.gone
import app.unduit.a2achatapp.helpers.visible
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.PropertyData
import app.unduit.a2achatapp.models.PropertyType
import com.bumptech.glide.Glide

class AgentPropertiesAdapter(
    val context: Context,
    private val listener: AdapterListener,
    private val propertyList: ArrayList<PropertyData>
) :
    RecyclerView.Adapter<AgentPropertiesAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAgentPropertiesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemAgentPropertiesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val propertyItem = propertyList[position]

        holder.binding.propertyPrice.text = propertyItem.property_type
        holder.binding.itemLocation.text = propertyItem.area_community
        holder.binding.bedrooms.text = propertyItem.bedrooms
        holder.binding.propertyTime.text = DateHelper.convertTimestampToTimeAgo(propertyItem.created_date.toLong())
        holder.binding.bathroom.text = "${propertyItem.bathrooms}"
        holder.binding.sqFeet.text = "${propertyItem.area_size}sqft"

        if(propertyItem.property_images?.isNotEmpty() == true) {
            Glide.with(holder.binding.itemPropertyImage).load(propertyItem.property_images?.get(0)).into(holder.binding.itemPropertyImage)
        }


        holder.binding.root.setOnClickListener {
            listener.onAdapterItemClicked("view_detail_property", position)
        }
        holder.binding.executePendingBindings()
    }


    override fun getItemCount(): Int {
        return propertyList.size // Return the number of items in your data list
    }

}
