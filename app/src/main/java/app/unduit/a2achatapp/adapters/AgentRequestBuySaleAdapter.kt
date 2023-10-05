package app.unduit.a2achatapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.databinding.ItemAgentRequestBuySaleBinding
import app.unduit.a2achatapp.helpers.DateHelper
import app.unduit.a2achatapp.helpers.addComma
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.PropertyData

class AgentRequestBuySaleAdapter(
    val context: Context,
    private val listener: AdapterListener,
    private val propertyList: ArrayList<PropertyData>
) :
    RecyclerView.Adapter<AgentRequestBuySaleAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAgentRequestBuySaleBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemAgentRequestBuySaleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val propertyItem = propertyList[position]

        holder.binding.propertyPrice.text = propertyItem.property_type
        holder.binding.itemLocation.text = propertyItem.area_community
        holder.binding.bedrooms.text = propertyItem.bedrooms
        holder.binding.propertyTime.text = DateHelper.convertTimestampToTimeAgo(propertyItem.created_date.toLong())
        holder.binding.bathroom.text = "${propertyItem.bathrooms}"
        holder.binding.sqFeet.text = "${propertyItem.property_size_min} - ${propertyItem.property_size_max}sqft"
        holder.binding.budget.text = "${addComma(propertyItem.budget_min)} - ${addComma(propertyItem.budget_max)} budget"


        holder.binding.root.setOnClickListener {
            listener.onAdapterItemClicked("view_detail_request", position)
        }
        holder.binding.executePendingBindings()
    }


    override fun getItemCount(): Int {
        return propertyList.size // Return the number of items in your data list
    }

}
