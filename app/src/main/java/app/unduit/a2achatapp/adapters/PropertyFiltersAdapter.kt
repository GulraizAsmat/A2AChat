package app.unduit.a2achatapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.ItemPropertyFiltersBinding
import app.unduit.a2achatapp.databinding.ItemPropertyTypeBinding
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
            holder.binding.clItem.setBackgroundResource(R.drawable.bg_btn_login)
            holder.binding.itemName.setTextColor(ContextCompat.getColor(context,R.color.color_white_shade_1))
        }
        else {
            holder.binding.clItem.setBackgroundResource(R.drawable.bg_btn_login_disabled)
            holder.binding.itemName.setTextColor(ContextCompat.getColor(context,R.color.color_black_shade_1))
        }

        holder.binding.clItem.setOnClickListener {

            listener.onAdapterItemClicked("click_on_item",position)

        }
        holder.binding.executePendingBindings()
    }


    override fun getItemCount(): Int {
        return propertyTypeList.size // Return the number of items in your data list
    }

}
