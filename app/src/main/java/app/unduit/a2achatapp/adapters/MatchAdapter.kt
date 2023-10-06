package app.unduit.a2achatapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.ItemAgentPropertiesBinding
import app.unduit.a2achatapp.databinding.ItemAgentPropertiesBuySaleBinding
import app.unduit.a2achatapp.databinding.ItemAgentRequestBuySaleBinding
import app.unduit.a2achatapp.databinding.ItemAgentRequestsBinding
import app.unduit.a2achatapp.databinding.ItemMatchChatBinding
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

class MatchAdapter(
    val context: Context,
    private val listener: AdapterListener,
    private val propertyList: ArrayList<PropertyData>
) :
    RecyclerView.Adapter<MatchAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemMatchChatBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMatchChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val propertyItem = propertyList[position]

            Log.e("Tag213","User Iamge "+ propertyItem.user_picture)
            Log.e("Tag213","sender_image  "+ propertyItem.sender_image)

        if(propertyItem.user_id==Const.userId){
            if(propertyItem.sender_image!!.isNotEmpty()) {

                    holder.binding.userName.text=propertyItem.sender_name
                Glide.with(holder.binding.profileImage).load(propertyItem.sender_image)
                    .into(holder.binding.profileImage)
            }

        }
        else {
            if(propertyItem.user_picture!!.isNotEmpty()) {
                holder.binding.userName.text=propertyItem.user_name

                Glide.with(holder.binding.profileImage).load(propertyItem.user_picture)
                    .into(holder.binding.profileImage)
            }

        }



        holder.binding.root.setOnClickListener {
            listener.onAdapterItemClicked("chat_detail", position)
        }
        holder.binding.executePendingBindings()
    }


    override fun getItemCount(): Int {
        return propertyList.size // Return the number of items in your data list
    }

}
