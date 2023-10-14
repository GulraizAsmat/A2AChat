package app.unduit.a2achatapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import app.unduit.a2achatapp.PurchaseLandFragmentArgs
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.adapters.ChatAdapter
import app.unduit.a2achatapp.adapters.MatchAdapter
import app.unduit.a2achatapp.databinding.FragmentChatBinding
import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.helpers.Const.tempValue
import app.unduit.a2achatapp.helpers.ProgressDialog
import app.unduit.a2achatapp.helpers.showToast
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.PropertyData
import com.bumptech.glide.Glide
import app.unduit.a2achatapp.models.chatList.Chat
import app.unduit.a2achatapp.models.chatList.ChatList
import com.employee.employeejobbie.models.chatList.ChatListData
import com.employee.employeejobbie.models.userChatDetails.UserChatDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject


class ChatFragment : Fragment() ,AdapterListener{
    var TAG="ChatFragment"
    private lateinit var binding: FragmentChatBinding
    val args: PurchaseLandFragmentArgs by navArgs()



    private var propertyData: PropertyData? = null
    private var chatList= ArrayList<Chat>()
    private var tempChatList = java.util.ArrayList<ChatList>()



    private lateinit var auth: FirebaseAuth




    private var matchList=ArrayList<PropertyData>()


    private val progressDialog by lazy {

        ProgressDialog(requireContext())
    }


    private val chatAdapter by lazy {
        chatList?.let {
            ChatAdapter(
                requireContext(),
                this,
                it
            )
        }
    }


    private val matchAdapter by lazy {
        MatchAdapter(
            requireContext(),
            this,
            matchList
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    fun init(){
        if(tempValue){
            Const.screenName=""
        }else {
            Const.screenName="chat"
        }

        recyclerviewManagers()
        getMatchData()
        loadUserProfileImage()
        getChatList()
        listeners()
//        dummyCode()
    }

    fun listeners(){
        binding.backIcon.setOnClickListener {

            requireActivity().onBackPressed()
        }
    }


    private fun recyclerviewManagers(){
        matchRecyclerView()
        chatRecyclerView()
    }

    private fun matchRecyclerView(){
        binding.rvMatch.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvMatch.itemAnimator = DefaultItemAnimator()

        binding.rvMatch.adapter = matchAdapter
    }
    private fun chatRecyclerView(){
        binding.rvChats.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvChats.itemAnimator = DefaultItemAnimator()

        binding.rvChats.adapter = chatAdapter
    }



    private fun getMatchData(){
            matchList.clear()
        progressDialog.progressBarVisibility(true)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser?.let { cUser ->
            val db = Firebase.firestore
            val ref =db.collection("requests/${cUser.uid}/posts").whereEqualTo("property_status","matched")

            ref.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.e(TAG, "${document.id} => ${document.data}")

                        matchList.add(document.toObject(PropertyData::class.java))
                    }

                    matchList.forEach { it.sender_id
                        Log.e("Tagh234","Sender id : "+ it.sender_id)
                    }


                    val list =matchList.filter { it.sender_id!=Const.userId }.sortedByDescending { it.created_date  }.distinctBy { it.sender_id }

                    matchList.clear()
                    matchList.addAll(list)
                    matchAdapter.notifyDataSetChanged()

                    progressDialog.progressBarVisibility(false)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                    requireContext().showToast("An error occurred. Please try again later")
                    progressDialog.progressBarVisibility(false)
                }
        }




    }




    private fun getChatList() {


        val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Chat")
            .child(Const.userId)


        dbRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                var senderId = ""
                var mainJsonArray = JSONArray()



//                com.employee.employeejobbie.helper.Log.e(screenName, "Value is: $snapshot")

//                Log.e("Count ", "childrenCount " + snapshot.childrenCount)
                snapshot.children.forEach { it ->


                    Log.e("Tag134", " data " + it.key)
                    senderId = it.key.toString()


                    val dataJsonArray = JSONArray()








                    it.child("conversation").children.forEach { data ->

                        Log.e("Tag134", "*** it.children **** "  )


                        var time=data.child("created").value
                        try {
                            val value =time.toString().split(".")



                            time=value[0]+value[1].substring(0, 9)

                        }
                        catch (ex:Exception){

                        }


                        val dataJson = JSONObject()
                        dataJson.put("sender_id", data.child("senderID").value)
                        dataJson.put("sender_msg_id", data.child("senderID").value)
                        dataJson.put("sender_name", data.child("senderName").value)
                        dataJson.put("sender_image", data.child("senderImage").value)
                        dataJson.put("message", data.child("content").value)
                        dataJson.put("seen_status", data.child("isSeen").value)
                        dataJson.put("message_type", data.child("msgKind").value)
                        dataJson.put("file_size", data.child("pdfSize").value)
                        dataJson.put("file_url", data.child("pdfUrl").value)
                        dataJson.put("receiverId", data.child("receiverId").value)
                        dataJson.put("receiverName", data.child("receiverName").value)
                        dataJson.put("receiverImage", data.child("receiverImage").value)
                        dataJson.put("time", time)

                        dataJsonArray.put(dataJson)
                    }

                    Log.e("Tag124"," Last Seen "+                    it.child("lastseen").child("isSeen").value)

                    val chatJson = JSONObject()
//                    Log.e("Tag134", "*** chatJson ****")
                    chatJson.put("sender_id", senderId)
                    chatJson.put("last_seen",  it.child("lastseen").child("isSeen").value)
                    chatJson.put("chat", dataJsonArray)


                    mainJsonArray.put(chatJson)

                }
                Log.e("Tag134", "*** main  ****")


                val json = JSONObject()
                json.put("chat_list", mainJsonArray)
                Log.e("Tag124q32", "json $json")


                val serverResponse = json.optJSONArray("chat_list")
                val customResponse = "{\"chat_list\":$serverResponse}"
                val chatListResponse = Gson().fromJson(customResponse, ChatListData::class.java)

                tempChatList.clear()
                tempChatList.addAll(chatListResponse.chat_list)


                Log.e("Tag124q32", "**************************")


                val tempList = ArrayList<Chat>()
                tempChatList.forEach {
                    try{
                        it.chat[it.chat.size - 1].seen_status=     it.last_seen
                        tempList.add( it.chat[it.chat.size - 1])
                    }catch (ex:Exception){

                    }



                }


                var userDb: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

                var userDetails = java.util.ArrayList<UserChatDetails>()
                userDb.addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.

                        snapshot.children.forEach { user ->


                            userDetails.add(UserChatDetails(user_id = user.key.toString(),
                                user_online_status = user.child("online").value,
                                user_image = user.child("image").value.toString(),
                                user_name = user.child("userName").value.toString()))


//                            tempList.forEach { chat->
//                                Log.e("chat1255",
//                                    "  user_name " + chat.sender_id)
//
//                            }


                        }

//                        Log.e("Tag134", "******************************** ")
                        userDetails.forEach { userDetails ->

//                            Log.e("Tag134", " User user_id " + userDetails.user_id)
//                            Log.e("Tag134", " User user_name " + userDetails.user_name)
//                            Log.e("Tag134",
//                                " User user_online_status " + userDetails.user_online_status)
//                            Log.e("Tag134", " User user_image " + userDetails.user_image)
//                            Log.e("Tag134", "----------------------------------")
                        }





//                        Log.e("chat1255",
//                            "  tempList.size " + tempList.size)

                        tempList.forEach { chat ->

                            userDetails.apply {
                                forEach { details ->

                                    if (chat.sender_id == details.user_id) {


                                        Log.e("chat1255",
                                            "  User name " + details.user_name)
                                        chat.sender_name = details.user_name
                                        chat.sender_image = details.user_image
                                        chat.user_online_status = details.user_online_status
                                        return@apply
                                    }
                                }


                            }

                        }


                        tempList.sortedByDescending { it.time }.forEach {

                            Log.e("Tag124q32", "sender id : " + it.sender_id)
                            Log.e("Tag124q32", "sender name  : " + it.sender_name)

                        }

                        chatList.clear()
                        chatList.addAll(tempList.sortedByDescending { it.time })

                        if(chatList.isEmpty()){
                            try{
//                                cl_main.visibility=View.GONE
//                                cl_empty_chat.visibility=View.VISIBLE
                            }
                            catch (ex:Exception){

                            }

                        }
                        else {
                            try{
//                                cl_main.visibility=View.VISIBLE
//                                cl_empty_chat.visibility=View.GONE
                            }
                            catch (ex:Exception){

                            }
                        }

                        chatAdapter!!.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                      Log.e(TAG,
                            "Failed to read value." + error.toException())
                    }

                })


            }

            override fun onCancelled(error: DatabaseError) {
             Log.e(TAG,
                    "Failed to read value." + error.toException())
            }

        })

    }
    
    private fun loadUserProfileImage() {
        val auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser?.let { cUser ->
            val db = Firebase.firestore
            val ref = db.collection("users").document(cUser.uid)
            Const.userId =cUser.uid
            ref.get().addOnSuccessListener { snapshot ->
                snapshot?.let {
                    val data = it.data

                    binding.userName.text= (data?.get("name") as String?).toString()








                    try {
                        val image = data?.get("profile_image") as String
                        Glide.with(this).load(image)
                            .fallback(R.drawable.ic_deafult_profile_icon)
                            .placeholder(R.drawable.ic_deafult_profile_icon)
                            .into(binding.profileImage)

                    }catch (ex:Exception){

                    }

                }
            }
        }




    }

    override fun onAdapterItemClicked(key: String, position: Int) {
        when(key){





            "chat_detail"->{

                if(Const.userId== matchList[position].user_id){
                    findNavController().navigate(ChatFragmentDirections.actionChatFragmentToChatFragment2(
                        matchList[position].sender_id, matchList[position].sender_name,matchList[position].sender_image ,matchList[position],false
                    ))
                }else {
                    findNavController().navigate(ChatFragmentDirections.actionChatFragmentToChatFragment2(
                        matchList[position].user_id.toString(), matchList[position].user_name.toString(),matchList[position].user_picture.toString() ,matchList[position],false
                    ))
                }


            }
            "chat_detail_with_chat"->{
                if(Const.userId== chatList[position].sender_id){
                    findNavController().navigate(ChatFragmentDirections.actionChatFragmentToChatFragment2(
                        chatList[position].receiverId, chatList[position].receiverName,chatList[position].receiverImage ,matchList[position],false
                    ))
                }else {
                    findNavController().navigate(ChatFragmentDirections.actionChatFragmentToChatFragment2(
                        chatList[position].sender_id, chatList[position].sender_name,chatList[position].sender_image ,matchList[position],false
                    ))
                }


            }
        }

    }


}