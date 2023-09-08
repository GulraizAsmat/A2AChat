package app.unduit.a2achatapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.ItemPropertyTypeBinding
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.PropertyType

class PropertyTypeItemAdapter(
    val context: Context,
    private val listener: AdapterListener,
    private val propertyTypeList: ArrayList<PropertyType>
) :
    RecyclerView.Adapter<PropertyTypeItemAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPropertyTypeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPropertyTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.itemName.text=propertyTypeList[position].name
        holder.binding.itemIcon.setImageResource(propertyTypeList[position].image)
        holder.binding.executePendingBindings()


        if(propertyTypeList[position].selected){
            holder.binding.clItem.setBackgroundResource(R.drawable.ic_light_orange_bg_1)
        }
        else {
            holder.binding.clItem.setBackgroundResource(R.color.color_white_shade_1)
        }

        holder.binding.clItem.setOnClickListener {

            listener.onAdapterItemClicked("click_on_item",position)

        }

    }


    override fun getItemCount(): Int {
        return propertyTypeList.size // Return the number of items in your data list
    }

}
