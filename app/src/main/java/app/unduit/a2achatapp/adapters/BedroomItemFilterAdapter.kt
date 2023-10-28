package app.unduit.a2achatapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.ItemBedBathBinding

import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.BathBedType
import app.unduit.a2achatapp.models.PropertyType

class BedroomItemFilterAdapter(
    val context: Context,
    private val bathBedTypeList: ArrayList<BathBedType>,
    private val listener: (position: Int) -> Unit,
) :
    RecyclerView.Adapter<BedroomItemFilterAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemBedBathBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemBedBathBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.itemName.text=bathBedTypeList[position].name



        if(bathBedTypeList[position].selected){
            holder.binding.cvItem.setBackgroundResource(R.drawable.bg_btn_light_purple_suqare_corners)
            holder.binding.itemName.setTextColor(ContextCompat.getColor(context,R.color.color_white_shade_1))
        }
        else {
            holder.binding.cvItem.setBackgroundResource(R.drawable.bg_btn_login_disabled)
            holder.binding.itemName.setTextColor(ContextCompat.getColor(context,R.color.color_white_shade_1))
        }

        holder.binding.cvItem.setOnClickListener {

            listener(position)

        }
        holder.binding.executePendingBindings()
    }


    override fun getItemCount(): Int {
        return bathBedTypeList.size // Return the number of items in your data list
    }

}
