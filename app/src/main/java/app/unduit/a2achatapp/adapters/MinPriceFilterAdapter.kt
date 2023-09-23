package app.unduit.a2achatapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.ItemBedBathBinding
import app.unduit.a2achatapp.databinding.ItemPriceBinding

import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.BathBedType
import app.unduit.a2achatapp.models.PropertyType

class MinPriceFilterAdapter(
    val context: Context,
    private val listener: AdapterListener,
    private val bathBedTypeList: ArrayList<BathBedType>
) :
    RecyclerView.Adapter<MinPriceFilterAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPriceBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPriceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.itemPrice.text=bathBedTypeList[position].name


        if(bathBedTypeList[position].selected){
            holder.binding.main.setBackgroundResource(R.color.color_purple_shade_7)
            holder.binding.itemPrice.setTextColor(ContextCompat.getColor(context,R.color.color_black_shade_1))
        }
        else {
            holder.binding.main.setBackgroundResource(R.color.white)
            holder.binding.itemPrice.setTextColor(ContextCompat.getColor(context,R.color.color_black_shade_1))
        }

        holder.binding.main.setOnClickListener {

            listener.onAdapterItemClicked("click_on_min_price",position)


        }
        holder.binding.executePendingBindings()
    }


    override fun getItemCount(): Int {
        return bathBedTypeList.size // Return the number of items in your data list
    }

}
