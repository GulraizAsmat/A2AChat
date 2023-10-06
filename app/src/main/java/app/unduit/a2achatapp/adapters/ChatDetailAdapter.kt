package app.unduit.a2achatapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.ItemChatDetailBinding
import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.helpers.Utils
import app.unduit.a2achatapp.listeners.AdapterListener

import app.unduit.a2achatapp.models.chatList.Chat



import java.util.*

class ChatDetailAdapter(
    val context: Context,
    val listener: AdapterListener,
    private val chatList: ArrayList<Chat>,
) :
    RecyclerView.Adapter<ChatDetailAdapter.ViewHolder>(), View.OnClickListener {

        var status :Boolean=false
    inner class ViewHolder(val binding: ItemChatDetailBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatDetailAdapter.ViewHolder {
        val binding =
            ItemChatDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return chatList.size

//        return  10
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ChatDetailAdapter.ViewHolder, position: Int) {

          
        
try{
    if(chatList.size>1){



        if(position>0) {
            if (Utils.getDate(chatList[position].time.toDouble()) != Utils.getDate(chatList[position - 1].time.toDouble())) {

                Log.e("getDateCurrentTimeZone", "date message ${Utils.getDate(chatList[position].time.toDouble())} :: ${Utils.getDate(chatList[position-1].time.toDouble())}")

                holder.binding.itemChatDate.visibility=View.VISIBLE
                holder.binding.itemChatDate.text = "----- "+    Utils.getDay(chatList[position].time.toDouble()) +" -----"

            }else {
                holder.binding.itemChatDate.visibility=View.GONE
            }
        }else {
            holder.binding.itemChatDate.visibility=View.VISIBLE
            holder.binding.itemChatDate.text =
                "----- "+    Utils.getDay(chatList[position].time.toDouble()) +" -----"
        }
    }


    else {

        holder.binding.itemChatDate.visibility=View.VISIBLE
        holder.binding.itemChatDate.text =
            "----- "+    Utils.getDay(chatList[position].time.toDouble()) +" -----"


    }

}
catch (ex:Exception){

}

        Log.e("Tag124"," user id "+ Const.userId +" sender id "+ chatList[position].sender_msg_id)



        if(Const.userId==chatList[position].sender_msg_id){

            if(position==chatList.size-1){

                if(status){
                    holder.binding.senderMsgTime.text="Read | "+Utils.getTime(chatList[position].time.toDouble())
                }else {
                    holder.binding.senderMsgTime.text="Sent | "+Utils.getTime(chatList[position].time.toDouble())
                }


            }else {
                holder.binding.senderMsgTime.text=Utils.getTime(chatList[position].time.toDouble())
            }


            if(chatList[position].message_type=="text"){
                holder.binding.clSender.visibility=View.VISIBLE
                holder.binding.clReceiver.visibility=View.GONE
                holder.binding.sender.text=chatList[position].message



            }




        }
        else {
            if(chatList[position].message_type=="text"){
                holder.binding.clSender.visibility=View.GONE
                holder.binding.clReceiver.visibility=View.VISIBLE
                holder.binding.receiver.text=chatList[position].message

            }else{

            }



            try {
                holder.binding.receiverMsgTime.text=Utils.getTime(chatList[position].time.toDouble())
            }catch (Ex:Exception){

            }


        }


    }

    fun listeners() {

    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.item_view -> {
                listener.onAdapterItemClicked("open", v.tag as Int)
            }


        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun getSeenStatus(seenStatus:Boolean){
        status=seenStatus
    }

}