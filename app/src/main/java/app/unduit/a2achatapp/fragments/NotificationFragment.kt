package app.unduit.a2achatapp.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.adapters.NotificationListAdapter
import app.unduit.a2achatapp.databinding.FragmentNotificationBinding
import app.unduit.a2achatapp.fcm.FcmNotificationSend
import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.helpers.DateHelper
import app.unduit.a2achatapp.helpers.ProgressDialog
import app.unduit.a2achatapp.helpers.SharedPref
import app.unduit.a2achatapp.helpers.showToast
import app.unduit.a2achatapp.listeners.AdapterListener

import app.unduit.a2achatapp.models.PropertyData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject


class NotificationFragment : Fragment() ,View.OnClickListener ,AdapterListener{

    private val TAG = "NotificationFragment"

    private lateinit var binding: FragmentNotificationBinding



    private lateinit var auth: FirebaseAuth

    var propertylist=ArrayList<PropertyData>()

    private val progressDialog by lazy {
        ProgressDialog(requireContext())
    }

    private val notificationListAdapter by lazy {
        NotificationListAdapter(requireContext(),
            this,
            propertylist,
       )
    }


    override fun onResume() {
        super.onResume()
        Const.screenName="notification_icon"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(inflater)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){

        listeners()
        recyclerViewManager()
        getNotificationData()



    }


    private fun recyclerViewManager(){

        binding.rvNotifications.layoutManager =
            LinearLayoutManager(context)
        binding.rvNotifications.itemAnimator = DefaultItemAnimator()
        binding.rvNotifications.adapter = notificationListAdapter
    }

    private fun listeners(){
        binding.btnBack.setOnClickListener(this)
    }


    private fun getNotificationData(){
        propertylist.clear()
        progressDialog.progressBarVisibility(true)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser?.let { cUser ->
            val db = Firebase.firestore

            val ref = db.collection("notifications/${cUser.uid}/posts")

            ref.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.e(TAG, "${document.id} => ${document.data}")

                        propertylist.add(document.toObject(PropertyData::class.java))
                    }
                  val list=  propertylist.sortedByDescending { it.created_date }
                    propertylist.clear()
                    propertylist.addAll(list)

                    notificationListAdapter.notifyDataSetChanged()
                    SharedPref.setInt(requireContext(),Const.matchCount,0)
                    progressDialog.progressBarVisibility(false)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                    requireContext().showToast("An error occurred. Please try again later")
                    progressDialog.progressBarVisibility(false)
                }
        }


    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_back->{
                requireActivity().onBackPressed()
            }



        }
    }


    private fun matchRequest(position: Int){

            progressDialog.progressBarVisibility(true)

        val db = FirebaseFirestore.getInstance()

        auth = Firebase.auth
        val currentUser = auth.currentUser



        if (currentUser != null) {
            Log.e("Tag21345","Created Sender user  "+ currentUser.uid)
            val currentTimeMillis = System.currentTimeMillis()
            var path ="requests/${currentUser.uid}/match/${currentTimeMillis.toString()}"
            val collectionReference = db.collection("requests").document(currentUser.uid).collection("posts").document(propertylist[position].uid)
            propertylist[position].property_status="matched"
            propertylist[position].created_date=currentTimeMillis.toString()
            collectionReference.set(propertylist[position])
                .addOnSuccessListener { documentReference ->
                    // Data added successfully
                    // You can get the document ID using documentReference.id
                    Log.e("Tafg213","Uplaod")
                    sendNotification(propertylist[position])
                    moveToSenderMatch(position) // hassam
                    matchRequestUpdateStatus(position) // gull
                    notificationStatusUpdate(position)
                    notificationSend(position)


//                    matchRequestNotificationForSenderUpdateStatus(position) // notif
                    // Handle success here
                }
                .addOnFailureListener { e ->
                    progressDialog.progressBarVisibility(false)
                    // Handle errors here
                    Log.e("Tafg213", "Fail$e")
                }
        }



    }

    private fun sendNotification(data:PropertyData){


        val notificationJson = JSONObject()

        notificationJson.put("title",
            "Request Matched" )
        notificationJson.put("body", "${data.user_name} accept the your request for ${data.property_type}" )


        val jsonMain = JSONObject()

        jsonMain.put("to", data.sender_fcm )
        jsonMain.put("notification", notificationJson)



        Log.e("TAG123","USER oOFFLINE "+jsonMain);





        FcmNotificationSend.post("https://fcm.googleapis.com/fcm/send", jsonMain, {
            Log.e("Notif","SEnd happy mode")
        }, {
            Log.e("Notif","SEnd happy Error")
        })
    }



    private fun matchRequestUpdateStatus(position: Int){    // gull

        progressDialog.progressBarVisibility(true)

        val db = FirebaseFirestore.getInstance()

        auth = Firebase.auth
        val currentUser = auth.currentUser



        if (currentUser != null) {
            Log.e("Tag21345","Created Sender user  "+ currentUser.uid)
            val currentTimeMillis = System.currentTimeMillis()
            var path ="requests/${currentUser.uid}/match/${currentTimeMillis.toString()}"
            val collectionReference = db.collection("requests").document(currentUser.uid).collection("posts").document(propertylist[position].uid)
            propertylist[position].property_status="matched"
            propertylist[position].created_date=currentTimeMillis.toString()
            collectionReference.set(propertylist[position])
                .addOnSuccessListener { documentReference ->
                    // Data added successfully
                    // You can get the document ID using documentReference.id
                    Log.e("Tafg213","Status has been updated")

                    notificationListAdapter.notifyItemChanged(position)



                    // Handle success here
                }
                .addOnFailureListener { e ->
                    progressDialog.progressBarVisibility(false)
                    // Handle errors here
                    Log.e("Tafg213", "Fail$e")
                }
        }



    }



    private fun notificationStatusUpdate(position: Int){

        progressDialog.progressBarVisibility(true)

        val db = FirebaseFirestore.getInstance()

        auth = Firebase.auth
        val currentUser = auth.currentUser




        if (currentUser != null) {
            Log.e("Tag21345","Created Sender user  "+ currentUser.uid)
            val currentTimeMillis = System.currentTimeMillis()
            var path ="requests/${currentUser.uid}/match/${currentTimeMillis.toString()}"
            val collectionReference = db.collection("notifications").document(currentUser.uid).collection("posts").document(propertylist[position].uid)
            propertylist[position].property_status="matched"
            propertylist[position].created_date=currentTimeMillis.toString()
            collectionReference.set(propertylist[position])
                .addOnSuccessListener { documentReference ->
                    // Data added successfully
                    // You can get the document ID using documentReference.id
                    Log.e("Tafg213","Status has been updated")




                    // Handle success here
                }
                .addOnFailureListener { e ->
                    progressDialog.progressBarVisibility(false)
                    // Handle errors here
                    Log.e("Tafg213", "Fail$e")
                }
        }
    }


    private fun notificationSend(position: Int){

        progressDialog.progressBarVisibility(true)

        val db = FirebaseFirestore.getInstance()

        auth = Firebase.auth
        val currentUser = auth.currentUser




        if (currentUser != null) {
            Log.e("Tag21345","Created Sender user  "+ currentUser.uid)
            val currentTimeMillis = System.currentTimeMillis()
            var path ="requests/${currentUser.uid}/match/${currentTimeMillis.toString()}"
            val collectionReference = db.collection("notifications").document(propertylist[position].sender_id.toString()).collection("posts").document(propertylist[position].uid)
            propertylist[position].property_status="request_matched"
            propertylist[position].created_date=currentTimeMillis.toString()
            collectionReference.set(propertylist[position])
                .addOnSuccessListener { documentReference ->
                    // Data added successfully
                    // You can get the document ID using documentReference.id
                    Log.e("Tafg213","Notification  Send ")

                    propertylist[position].property_status="matched"
                    notificationListAdapter.notifyItemChanged(position)

                    progressDialog.progressBarVisibility(false)

                    // Handle success here
                }
                .addOnFailureListener { e ->
                    progressDialog.progressBarVisibility(false)
                    // Handle errors here
                    Log.e("Tafg213", "Fail$e")
                }
        }
    }



    private fun matchRequestNotificationForSenderUpdateStatus(position: Int){

        progressDialog.progressBarVisibility(true)

        val db = FirebaseFirestore.getInstance()

        auth = Firebase.auth
        val currentUser = auth.currentUser



        if (currentUser != null) {
            Log.e("Tag21345","Created Sender user  "+ currentUser.uid)
            val currentTimeMillis = System.currentTimeMillis()
            var path ="requests/${currentUser.uid}/match/${currentTimeMillis.toString()}"
            val collectionReference = db.collection("requests").document(currentUser.uid).collection("send").document(propertylist[position].uid)
            propertylist[position].property_status="matched"
            propertylist[position].created_date=currentTimeMillis.toString()
            collectionReference.set(propertylist[position])
                .addOnSuccessListener { documentReference ->
                    // Data added successfully
                    // You can get the document ID using documentReference.id
                    Log.e("Tafg213","Status has been updated")

                    notificationListAdapter.notifyItemChanged(position)



                    // Handle success here
                }
                .addOnFailureListener { e ->
                    progressDialog.progressBarVisibility(false)
                    // Handle errors here
                    Log.e("Tafg213", "Fail$e")
                }
        }



    }

    @SuppressLint("SuspiciousIndentation")
    private fun moveToSenderMatch(position: Int){ // hassam user
        val db = FirebaseFirestore.getInstance()

        auth = Firebase.auth
        val currentUser = auth.currentUser


        Log.e("Tag21345","Created Property user  "+ propertylist[position].user_id.toString())
        if (currentUser != null) {
            val currentTimeMillis = System.currentTimeMillis()
            var path ="requests/${currentUser.uid}/sender/${currentTimeMillis.toString()}"
            val collectionReference = db.collection("requests").document(propertylist[position].sender_id.toString()).collection("posts").document(propertylist[position].uid)
            propertylist[position].property_status="matched"

            collectionReference.set(propertylist[position])
                .addOnSuccessListener { documentReference ->
                    // Data added successfully
                    // You can get the document ID using documentReference.id
                    progressDialog.progressBarVisibility(false)
                    Log.e("Tafg213","Match has been successfully")

                    // Handle success here
                }
                .addOnFailureListener { e ->
                    progressDialog.progressBarVisibility(false)
                    // Handle errors here
                    Log.e("Tafg213", "Fail$e")
                }
        }



    }
    private fun makePhoneCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }

    private fun whatsapp(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://wa.me/$phoneNumber")
        startActivity(intent)
        // Verify if WhatsApp is installed on the device

    }


    override fun onAdapterItemClicked(key: String, position: Int) {
            when(key){
                "match_request"->{
                    matchRequest(position)
                }
                "detail"->{
                    Log.e("Tag3","Sender Name "+ propertylist[position].sender_name)
                    Const.PropertyType="match"
                    Const.screenName=""
                    findNavController().navigate(NotificationFragmentDirections.actionNotificationFragmentToPropertyDetailFragment(propertylist[position],"matches"))
                }
               "phone"-> {
                    if (Const.userId == propertylist[position]!!.user_id) {
                        makePhoneCall(propertylist[position]!!.sender_phone)
                    } else {
                        makePhoneCall(propertylist[position]!!.user_phone)
                    }
                }

                "whatsapp" -> {
                    if (Const.userId == propertylist[position]!!.user_id) {
                        whatsapp(propertylist[position]!!.sender_whatsapp)
                    } else {
                        whatsapp(propertylist[position]!!.sender_whatsapp)
                    }
                }

              "chat" -> {
                    if (Const.userId == propertylist[position]!!.user_id) {
                        findNavController().navigate(
                            NotificationFragmentDirections.actionNotificationFragmentToChatFragment2(
                                propertylist[position]!!,false,
                                propertylist[position]!!.sender_id.toString(), propertylist[position]!!.sender_name!!, propertylist[position]!!.sender_image!!
                            )
                        )
                    }else {
                        findNavController().navigate(
                            NotificationFragmentDirections.actionNotificationFragmentToChatFragment2(
                                propertylist[position]!!,false,
                                propertylist[position]!!.user_id.toString(), propertylist[position]!!.user_name!!, propertylist[position]!!.user_picture!!
                            )
                        )
                    }



                }
            }
    }


}