package app.unduit.a2achatapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.ItemPropertyFiltersBinding
import app.unduit.a2achatapp.databinding.ItemPropertyTypeBinding
import app.unduit.a2achatapp.helpers.gone
import app.unduit.a2achatapp.helpers.visible
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.PropertyType

class PropertyFiltersAdapter(
    val context: Context,
    private val listener: AdapterListener,
    private val propertyTypeList: ArrayList<PropertyType>
) :
    RecyclerView.Adapter<PropertyFiltersAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPropertyFiltersBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPropertyFiltersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.itemName.text=propertyTypeList[position].name

        if(propertyTypeList[position].selected){
            holder.binding.clItem.setBackgroundResource(R.drawable.bg_btn_40_light_purple_suqare_corners_dark)
            holder.binding.ivCancel.visible()
        }
        else {
            holder.binding.clItem.setBackgroundResource(R.drawable.bg_btn_purple_border)
            holder.binding.ivCancel.gone()
        }

        holder.binding.clItem.setOnClickListener {
            listener.onAdapterItemClicked("click_on_item",position)
        }
        holder.binding.ivCancel.setOnClickListener {
            listener.onAdapterItemClicked("cancel_filter",position)
        }
        holder.binding.executePendingBindings()
    }


    override fun getItemCount(): Int {
        return propertyTypeList.size // Return the number of items in your data list
    }

}
