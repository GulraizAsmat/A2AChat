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
import app.unduit.a2achatapp.adapters.AgentPropertiesBuySaleAdapter
import app.unduit.a2achatapp.adapters.ChatAdapter
import app.unduit.a2achatapp.adapters.MatchAdapter
import app.unduit.a2achatapp.databinding.FragmentChatBinding
import app.unduit.a2achatapp.databinding.FragmentPurchaseLandBinding
import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.helpers.ProgressDialog
import app.unduit.a2achatapp.helpers.showToast
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.MessageData
import app.unduit.a2achatapp.models.PropertyData
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ChatFragment : Fragment() ,AdapterListener{
    var TAG="ChatFragment"
    private lateinit var binding: FragmentChatBinding
    val args: PurchaseLandFragmentArgs by navArgs()



    private var propertyData: PropertyData? = null
    private var chatList= ArrayList<MessageData>()



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

        recyclerviewManagers()
        getMatchData()
        loadUserProfileImage()
        getChatList()
//        dummyCode()
    }

    fun listeners(){

    }


    private fun recyclerviewManagers(){
        matchRecyclerView()
        chatRecyclerView()
    }

    private fun matchRecyclerView(){
        binding.rvMatch.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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

        progressDialog.progressBarVisibility(true)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser?.let { cUser ->
            val db = Firebase.firestore
            val ref = db.collection("requests/${cUser.uid}/match")

            ref.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.e(TAG, "${document.id} => ${document.data}")

                        matchList.add(document.toObject(PropertyData::class.java))
                    }
                    val list =matchList.sortedByDescending { it.created_date }
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

    private fun getChatList(){
        auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser?.let { cUser ->
            val db = Firebase.firestore


            val database = FirebaseDatabase.getInstance()
            val myRef: DatabaseReference = database.reference.child("Chats").child(cUser.uid)

            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    Log.e("Tagq234", "dataSnapshot$dataSnapshot")
                    // Handle data retrieval here
                    if (dataSnapshot.exists()) {
                        for (childSnapshot in dataSnapshot.children) {
                            val key = childSnapshot.key
                            val value = childSnapshot.value
                            // Do something with the key and value
                            val chatMessage = value as Map<String, Any>
                            if (chatMessage != null) {

                                val senderId = chatMessage["senderId"].toString()
                                val senderName = chatMessage["senderName"] .toString()
                                val receiverId = chatMessage["receiverId"] .toString()
                                val created = chatMessage["created"] .toString()
                                val msgKind = chatMessage["msgKind"] .toString()
                                val receiverName = chatMessage["receiverName"] .toString()
                                val receiverImage = chatMessage["receiverImage"].toString()
                                val content = chatMessage["content"] .toString()
                                val seen = chatMessage["seen"].toString().toBoolean()
                                val senderImage = chatMessage["senderImage"] .toString()

                                // Create a ChatMessage object with the parsed data
                                val chatMessageObj = MessageData(
                                    senderId,
                                    senderName,
                                    receiverId,
                                    created.toString(),
                                    msgKind,
                                    receiverName,
                                    receiverImage,
                                    content,
                                    seen,
                                    senderImage
                                )

                                chatList.add(chatMessageObj)
                            }
                        }

                        chatAdapter!!.notifyDataSetChanged()
                    } else {
                        // Data doesn't exist
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error
                }
            })



            progressDialog.progressBarVisibility(false)
        }
    }

    fun dummyCode(){
        val firestorePath = "chats/LpckgCDNKQYvVA6Q1a0vbrBmydI2/chat_nodes/kZ2W4ifbP0hTLKIVj8nVtABL7u32/conversation"

// Split the path into segments
        val pathSegments = firestorePath.split("/")
        val db = Firebase.firestore
// Reference to the Firestore collection
        var query = db.collection(pathSegments[1])

// Iterate through the path segments to build the reference
        for (i in 2 until pathSegments.size-1 step 2) {
            val collectionName = pathSegments[i]
            val documentId = pathSegments[i + 1]

            query = query.document(documentId).collection(collectionName)
        }

// Finally, fetch the data from Firestore
        query.get()
            .addOnSuccessListener { documents ->

                Log.e("Raq122","Tag213   "+ documents.documents)
                for (document in documents) {
                    // Handle the data here
                    val data = document.data
                    Log.e("Raq122","Tag213   "+ document.data)
                    // Do something with the data
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Log.e("Raq122", "Error getting documents: $exception")
            }
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
                findNavController().navigate(ChatFragmentDirections.actionChatFragmentToChatFragment2(
                    matchList[position],false))
            }
        }

    }


}