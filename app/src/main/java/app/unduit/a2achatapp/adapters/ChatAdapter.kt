package app.unduit.a2achatapp.adapters

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.ItemChatBinding
import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.helpers.Const.userId
import app.unduit.a2achatapp.helpers.Utils
import app.unduit.a2achatapp.listeners.AdapterListener
import com.bumptech.glide.Glide
import app.unduit.a2achatapp.models.chatList.Chat

class ChatAdapter(
    val context: Context,
    private val listener: AdapterListener,
    private val chatList: ArrayList<Chat>
) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatItem = chatList[position]

        holder.binding.itemChatMessage.text=chatItem.message

        holder.binding.itemChatTime.text =
            Utils.getTimeAgo(chatList[position].time.toDouble())

        Log.e("lpl","userId ="+userId)
        Log.e("lpl","sender_id = "+chatItem.sender_id)



            if(Const.userId==chatItem.sender_id){
                Log.e("lpl","Sendewr Call ")
                holder.binding.itemChatName.text=chatItem.receiverName


                Glide.with(holder.binding.itemChatImage).load(chatItem.receiverImage)
                    .into(holder.binding.itemChatImage)


            }else {
                holder.binding.itemChatName.text=chatItem.sender_name

                Log.e("lpl","reciver Call ")

                Glide.with(holder.binding.itemChatImage).load(chatItem.sender_image)
                    .into(holder.binding.itemChatImage)
            }

        if(!chatList[position].seen_status){
            holder.binding.clChatCounter.visibility=View.VISIBLE
            val typeface: Typeface? = ResourcesCompat.getFont(context, R.font.hellix_bold)
            holder.binding.clChatCounter.visibility=View.VISIBLE
            holder.binding.itemChatTime.typeface = typeface
            holder.binding.itemChatName.typeface = typeface
        }
        else {
            holder.binding.clChatCounter.visibility=View.GONE
            val typeface: Typeface? = ResourcesCompat.getFont(context, R.font.hellix_regular)
            holder.binding.itemChatTime.typeface = typeface
            val typeface1: Typeface? = ResourcesCompat.getFont(context, R.font.hellix_semibold)
            holder.binding.itemChatName.typeface = typeface1
        }


        if(chatItem.user_online_status==true){
            holder.binding.itemOnlineIcon.visibility= View.VISIBLE
        }
        else {
            holder.binding.itemOnlineIcon.visibility= View.GONE
        }




        holder.binding.root.setOnClickListener {
            listener.onAdapterItemClicked("chat_detail_with_chat", position)
        }
        holder.binding.executePendingBindings()
    }


    override fun getItemCount(): Int {
        return chatList.size // Return the number of items in your data list
    }

}
