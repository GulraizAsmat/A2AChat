package app.unduit.a2achatapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.ItemPropertyListBinding
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.PropertyData
import com.bumptech.glide.Glide

class PropertyListAdapter(
    val context: Context,
    private val listener: AdapterListener,
    private val propertyList: ArrayList<PropertyData>
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





        if(propertyItem.property_status=="sender"){
            holder.binding.propertyType.text = propertyItem.property_type
            holder.binding.propertyPrice.text = "${propertyItem.price} AED"
            holder.binding.itemLocation.text = propertyItem.area_community
            holder.binding.bedrooms.text = propertyItem.bedrooms
            holder.binding.bathroom.text = propertyItem.bathrooms
            holder.binding.sqFeet.text = "${propertyItem.area_size}sqft"

                holder.binding.clContact.visibility=View.GONE
                holder.binding.pendingBtn.visibility=View.VISIBLE
        }


        if(propertyItem.property_images?.isNotEmpty() == true) {
            Glide.with(holder.binding.itemPropertyImage).load(propertyItem.property_images?.get(0)).into(holder.binding.itemPropertyImage)
        }
        // Set a click listener on the root view if needed
        holder.binding.root.setOnClickListener {
//            listener.onItemClick(propertyItem)
            listener.onAdapterItemClicked("", position)
        }

        // Execute pending bindings to update the UI
        holder.binding.executePendingBindings()
    }

}
