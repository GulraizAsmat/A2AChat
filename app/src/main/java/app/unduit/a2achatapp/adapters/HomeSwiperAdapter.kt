package app.unduit.a2achatapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.ItemCardSwipeBinding

import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.PropertyData


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
        val propertyData = homeSwipeList[position]

        holder.binding.itemImage.setImageResource(propertyData.image)
        holder.binding.itemBhk.text=propertyData.bhk
        holder.binding.itemSqft.text=propertyData.sqft
        holder.binding.itemLocation.text=propertyData.location

        // Set a click listener on the root view if needed
        holder.binding.viewDetail.setOnClickListener {
            listener.onAdapterItemClicked("",position)
        }

        // Execute pending bindings to update the UI
        holder.binding.executePendingBindings()
    }

}
