package app.unduit.a2achatapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import app.unduit.a2achatapp.adapters.AgentPropertiesBuySaleAdapter
import app.unduit.a2achatapp.databinding.FragmentChat2Binding
import app.unduit.a2achatapp.databinding.FragmentChatBinding
import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.helpers.showToast
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.MessageData
import app.unduit.a2achatapp.models.PropertyData
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class ChatDetailFragment : Fragment(),AdapterListener {

    var TAG="ChatDetailFragment"
    private lateinit var binding: FragmentChat2Binding
    private lateinit var auth: FirebaseAuth



    val args: ChatDetailFragmentArgs by navArgs()
    private var propertyData: PropertyData? = null
    val data = MessageData()









    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        binding = FragmentChat2Binding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    init()

    }

    private fun defaultData(){
        propertyData=args.propertyData
        if(Const.userId== propertyData!!.user_id){
            binding.userName.text= propertyData!!.sender_name
            Glide.with(binding.profileImage).load(propertyData!!.sender_image)
                .into(binding.profileImage)

        }else {
            binding.userName.text= propertyData!!.sender_name
            Glide.with(binding.profileImage).load(propertyData!!.user_picture)
                .into(binding.profileImage)
        }

    }

    fun init(){
        defaultData()

        binding.send.setOnClickListener {
            if(binding.message.text!!.isNotEmpty()){

                sendMessage(binding.message.text!!.toString())

            }

        }
    }

    fun listeners(){

    }

    private fun sendMessage(message:String){



        val db = FirebaseFirestore.getInstance()

        auth = Firebase.auth
        val currentUser = auth.currentUser






        auth = Firebase.auth


        if(currentUser!=null){
            val database = FirebaseDatabase.getInstance()
            val myRef: DatabaseReference = database.reference.child("Chats").child(currentUser.uid).child(
                propertyData!!.sender_id
            )




            data!!.content=message
            data.seen=false
            data.msgKind="text"

            data.senderId=currentUser.uid
            data.senderName= propertyData!!.user_name!!
            data.senderImage= propertyData!!.user_picture!!

            data.receiverId=propertyData!!.sender_id
            data.receiverName= propertyData!!.sender_name
            data.receiverImage= propertyData!!.sender_image

            data.created=System.currentTimeMillis().toString()

            myRef.push().setValue(data).addOnSuccessListener {
                Log.e("Tag23"," user id "+currentUser.uid)
                val myRef1: DatabaseReference = database.reference.child("Chats").child(data.receiverId!!).child(currentUser.uid)
                myRef1.push().setValue(data).addOnSuccessListener {
                    Log.e("Tag2134","Data has been send")
                    binding.message.setText("")

                }.addOnFailureListener {


                }



            }.addOnFailureListener {


            }



        }







    }







    override fun onAdapterItemClicked(key: String, position: Int) {

    }


}