package app.unduit.a2achatapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.ItemPropertyTypeBinding
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.PropertyType

class PropertyTypeFilterAdapter(
    val context: Context,
    private val propertyTypeList: ArrayList<PropertyType>,
    private val listener: (position: Int) -> Unit,
) :
    RecyclerView.Adapter<PropertyTypeFilterAdapter.ViewHolder>() {

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



        if(propertyTypeList[position].selected){
            holder.binding.clItem.setBackgroundResource(R.drawable.bg_btn_light_purple_suqare_corners)
            holder.binding.itemName.setTextColor(ContextCompat.getColor(context,R.color.color_white_shade_1))
        }
        else {
            holder.binding.clItem.setBackgroundResource(R.drawable.bg_btn_login_disabled)
            holder.binding.itemName.setTextColor(ContextCompat.getColor(context,R.color.white))
        }

        holder.binding.clItem.setOnClickListener {

            listener(position)

        }
        holder.binding.executePendingBindings()
    }


    override fun getItemCount(): Int {
        return propertyTypeList.size // Return the number of items in your data list
    }

}
