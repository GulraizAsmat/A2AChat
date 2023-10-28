package app.unduit.a2achatapp

import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import app.unduit.a2achatapp.adapters.ChatDetailAdapter
import app.unduit.a2achatapp.databinding.FragmentChat2Binding
import app.unduit.a2achatapp.fcm.FcmNotificationSend
import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.MessageData
import app.unduit.a2achatapp.models.PropertyData
import com.bumptech.glide.Glide
import app.unduit.a2achatapp.models.chatList.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Logger

import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.util.ArrayList
import java.util.Locale
import java.util.UUID


class ChatDetailFragment : Fragment(),AdapterListener {

    var TAG="ChatDetailFragment"
    private lateinit var binding: FragmentChat2Binding
    private lateinit var auth: FirebaseAuth

    var receiverId=""
    var receiverName=""
    var receiverImage=""
    var receiverToken=""


    var senderId=""
    var senderName=""
    var senderImage=""
    var isMessageSend=true

    val args: ChatDetailFragmentArgs by navArgs()
    private var propertyData: PropertyData? = null
    var chatDetailList = ArrayList<Chat>()




    private val chatDetailAdapter by lazy {
        ChatDetailAdapter(requireContext(),
            this,
            chatDetailList)
    }

    override fun onResume() {
        super.onResume()
        updateLastStatusFirebase()
        updateLastSeenAdapter()

    }


    override fun onPause() {


        val database1: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat").child(Const.userId)
                .child(receiverId)
                .child("lastseen")



        val status = java.util.HashMap<String, Any>()
        status["isSeen"] = true
        database1.setValue(status).addOnSuccessListener {

        }

        super.onPause()
    }

    private fun updateLastStatusFirebase(){

        val database1: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat")
                .child(Const.userId)
                .child(receiverId)
                .child("lastseen")

        val status = java.util.HashMap<String, Any>()
        status["isSeen"] = true
        database1.setValue(status).addOnSuccessListener {
            updateLastSeenAdapter()
        }


    }

    private fun keyboardShown(rootView: View): Boolean {
        val softKeyboardHeight = 100
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)
        val dm = rootView.resources.displayMetrics
        val heightDiff: Int = rootView.bottom - r.bottom
        return heightDiff > softKeyboardHeight * dm.density
    }

    private fun checkKeyboardOpen() {
        try {

            binding.clMain.viewTreeObserver.addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener {

                try {
                    if (keyboardShown(    binding.clMain.rootView)) {
                        Log.e("keyboard", "keyboard UP")
                        if(chatDetailList.isNotEmpty()){
                            binding.rvChatsDetail.post {
                                binding.rvChatsDetail.smoothScrollToPosition(chatDetailAdapter.itemCount - 1)
                            }
                        }

                    } else {
                        Log.e("keyboard", "keyboard Down")

                    }
                } catch (ex: Exception) {

                }

            })
        } catch (ex: Exception) {

        }


    }

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


        senderId=Const.userId
        senderName=Const.userName
        senderImage=Const.userImage


        receiverId=args.chatUserId
        receiverName= args.chatUserName
        receiverImage= args.chatUserImage

        getChatUserDetail()
        binding.userName.text=args.chatUserName
        Glide.with(binding.profileImage).load(receiverImage)
            .into(binding.profileImage)






        chatDetailList.clear()
        chatDetailList.addAll(Const.chatDetailList)


        Log.e("Taf124", "size " + chatDetailList.size)
        chatDetailList.forEach {


            Log.e("Taf124", "Message" + it.message)
        }
        if(chatDetailList.isNotEmpty()){
            binding.rvChatsDetail.post {
                binding.rvChatsDetail.smoothScrollToPosition(chatDetailAdapter.itemCount - 1)
            }
        }
        chatDetailAdapter.notifyDataSetChanged()

        getChatDetail()


    }

    fun init(){
        Const.screenName=""
        defaultData()
        listeners()
        chatRecyclerViewManger()
        checkKeyboardOpen()
    }

    fun listeners(){
        binding.send.setOnClickListener {
            if(binding.message.text!!.isNotEmpty()){
                if(isMessageSend) {
                    isMessageSend=false
                    sendMessage(binding.message.text!!.toString())
                }

            }

        }

        binding.backIcon.setOnClickListener {

            requireActivity()!!.onBackPressed()
        }
    }

    private fun chatRecyclerViewManger() {


        binding.rvChatsDetail.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvChatsDetail.itemAnimator = DefaultItemAnimator()
        binding.rvChatsDetail.adapter = chatDetailAdapter


    }

    private fun getChatDetail() {


        val chatDetailDb: DatabaseReference = FirebaseDatabase.getInstance().getReference("Chat")
            .child(Const.userId)
            .child(receiverId).child("conversation")

        chatDetailDb.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                Log.e("Tagq32453", "Datac  $snapshot")

                chatDetailList.clear()
                snapshot.children.forEach { chat ->
                    Log.e("Tag123",
                        chat.key.toString())

                    Log.e("Tag123",
                        chat.child("senderID").value.toString())


                    var seenStatus = false

                    seenStatus = chat.child("isSeen").value.toString() == "true"


                    chatDetailList.add(
                        Chat(
                        sender_id = chat.child("senderID").value.toString(),
                        sender_name = chat.child("senderName").value.toString(),
                        sender_msg_id = chat.child("senderID").value.toString(),
                        file_url = chat.child("pdfUrl").value.toString(),
                        message = chat.child("content").value.toString(),
                        message_type = chat.child("msgKind").value.toString(),
                        file_size = chat.child("pdfSize").value.toString(),
                        time = chat.child("created").value.toString(),
                        seen_status = seenStatus


                    )
                    )
                }

                if(chatDetailList.isNotEmpty()) {


                    Handler(Looper.getMainLooper()).postDelayed({
                        // Your Code
//                            rv_chat_details.scrollToPosition(lastPosition-1)


                        if(chatDetailList.isNotEmpty()){
                            binding.rvChatsDetail.post {
                                binding.rvChatsDetail.smoothScrollToPosition(chatDetailAdapter.itemCount - 1)
                            }
                        }
                        chatDetailAdapter.notifyDataSetChanged()

                    }, 100)


                }


            }





            override fun onCancelled(error: DatabaseError) {
                Log.e("Tag213",
                    "Failed to read value." + error.toException())
            }

        })

    }


    private fun updateLastSeenAdapter(){
        val database2: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat")
                .child(receiverId)
                .child(Const.userId)
                .child("lastseen")

        database2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.


                try {
                    Log.e("Tag1234","mesg seen status "+ snapshot.child("isSeen").value)
                    chatDetailAdapter.getSeenStatus(snapshot.child("isSeen").value as Boolean)
                }
                catch (ex:Exception){

                }


                try {
                    chatDetailAdapter.notifyItemChanged(chatDetailList.size-1)
                }catch (ex:Exception){

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun sendMessage(message:String){

        val data= MessageData()

        val db = FirebaseFirestore.getInstance()

        auth = Firebase.auth
        val currentUser = auth.currentUser






        auth = Firebase.auth


        if(currentUser!=null){
            val database = FirebaseDatabase.getInstance()
            val myRef: DatabaseReference = database.reference.child("Chat").child(currentUser.uid).child(
                receiverId
            )

            val myMap = java.util.HashMap<String, Any>()
            val unixTime = System.currentTimeMillis() / 1000
            myMap["content"] = message
            myMap["created"] = unixTime
            myMap["id"] = UUID.randomUUID().toString().uppercase(Locale.getDefault())
            myMap["msgKind"] = "text"
            myMap["pdfSize"] = ""
            myMap["pdfUrl"] = ""

            myMap["senderID"] = senderId
            myMap["senderName"] = senderName
            myMap["senderImage"] =senderImage

            myMap["receiverId"] = receiverId
            myMap["receiverName"] =receiverName
            myMap["receiverImage"] = receiverImage
            myMap["isSeen"] = false



            myRef.child("conversation").push().setValue(myMap).addOnSuccessListener {







                val myRef1: DatabaseReference = database.reference.child("Chat").child(receiverId).child(currentUser.uid)
                myRef1.child("conversation").push().setValue(myMap).addOnSuccessListener {
                    Log.e("Tag2134","Data has been send")

                    val status = HashMap<String, Any>()
                    status["isSeen"] = false
                    myRef1.child("lastseen").setValue(status).addOnSuccessListener {

                    }

                    sendNotification()
                    binding.message.setText("")
                    isMessageSend=true
                }.addOnFailureListener {


                }



            }.addOnFailureListener {

            }

        }
    }



    private fun sendNotification(){


        val notificationJson = JSONObject()

        notificationJson.put("title",
            senderName )
        notificationJson.put("body", "${binding.message.text!!.toString()}" )


        val jsonMain = JSONObject()

        jsonMain.put("to", receiverToken)
        jsonMain.put("notification", notificationJson)



        Log.e("TAG123","USER oOFFLINE "+jsonMain);





        FcmNotificationSend.post("https://fcm.googleapis.com/fcm/send", jsonMain, {
            Log.e("Notif","SEnd happy mode")
        }, {
            Log.e("Notif","SEnd happy Error")
        })
    }

    private fun getChatUserDetail() {

        try {
            val db = Firebase.firestore
            val ref = db.collection("users").document(receiverId)


            ref.get().addOnSuccessListener { snapshot ->

                Log.e("Tag23", "user Data " + snapshot.data)

                snapshot?.let {
                    val data = it.data
                    receiverToken= data?.get("fcm_token").toString()



                }
            }
        }
        catch (ex:Exception){

        }

    }



    override fun onAdapterItemClicked(key: String, position: Int) {

    }


}